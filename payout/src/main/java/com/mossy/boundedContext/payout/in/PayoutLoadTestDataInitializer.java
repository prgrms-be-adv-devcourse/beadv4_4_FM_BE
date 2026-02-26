package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutSellerRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutUserRepository;
import com.mossy.shared.cash.enums.SellerEventType;
import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 정산 부하 테스트용 대용량 데이터 시더 (loadtest 프로파일 전용)
 *
 * 생성 데이터:
 *  - 판매자 20명  (ID: 301 ~ 320)
 *  - 구매자 100명 (ID: 401 ~ 500)
 *  - PayoutCandidateItem 30,000건 (주문 10,000건 × 3 이벤트 타입)
 *    - 정산__상품판매_대금   (payee = 판매자)
 *    - 정산__상품판매_수수료  (payee = SYSTEM, id=1)
 *    - 정산__상품판매_기부금  (payee = DONATION, id=3)
 *
 * 멱등성: 첫 번째 relId(1_000_001)의 후보가 이미 있으면 전체 skip
 */
@Slf4j
@Component
@Profile("loadtest")
@Order(2)  // PayoutDataInitializer(reserved sellers) 이후 실행
@RequiredArgsConstructor
public class PayoutLoadTestDataInitializer implements CommandLineRunner {

    // loadtest 전용 ID 범위 (PayoutDataInitializer의 범위와 겹치지 않도록)
    private static final long SELLER_ID_START = 301L;
    private static final int  SELLER_COUNT     = 20;
    private static final long BUYER_ID_START   = 401L;
    private static final int  BUYER_COUNT      = 100;
    private static final long ORDER_REL_ID_START = 1_000_001L;
    private static final int  ORDER_COUNT       = 10_000;

    private static final BigDecimal FEE_RATE      = new BigDecimal("0.20");
    private static final BigDecimal DONATION_RATE = new BigDecimal("0.10");

    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutUserRepository   payoutUserRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutRepository payoutRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (isAlreadySeeded()) {
            log.info("[LoadTestDataInitializer] 이미 시드 완료 - skip");
            return;
        }

        log.info("[LoadTestDataInitializer] 대용량 데이터 시드 시작");
        long startMs = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        // readyWaitingDays 이전 날짜로 설정해 배치가 즉시 처리할 수 있도록
        LocalDateTime paymentDate = now.minusDays(30);

        // ── 1. 판매자 / 구매자 생성 ──────────────────────────────────────
        for (long id = SELLER_ID_START; id < SELLER_ID_START + SELLER_COUNT; id++) {
            createSellerIfAbsent(id, now);
        }
        for (long id = BUYER_ID_START; id < BUYER_ID_START + BUYER_COUNT; id++) {
            createBuyerIfAbsent(id, now);
        }

        // 예약 판매자(SYSTEM=1, DONATION=3) 조회
        PayoutSeller system   = payoutSellerRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("SYSTEM seller(1) not found – run PayoutDataInitializer first"));
        PayoutSeller donation = payoutSellerRepository.findById(3L)
                .orElseThrow(() -> new IllegalStateException("DONATION seller(3) not found – run PayoutDataInitializer first"));

        // ── 2. 각 판매자의 활성 Payout 확보 ──────────────────────────────
        ensureActivePayout(system);
        ensureActivePayout(donation);
        for (long id = SELLER_ID_START; id < SELLER_ID_START + SELLER_COUNT; id++) {
            PayoutSeller seller = payoutSellerRepository.findById(id).orElseThrow();
            ensureActivePayout(seller);
        }

        // ── 3. PayoutCandidateItem 벌크 생성 ─────────────────────────────
        List<PayoutCandidateItem> batch = new ArrayList<>(300);

        for (int i = 0; i < ORDER_COUNT; i++) {
            long relId    = ORDER_REL_ID_START + i;
            long sellerId = SELLER_ID_START + (i % SELLER_COUNT);
            long buyerId  = BUYER_ID_START  + (i % BUYER_COUNT);

            PayoutSeller seller = payoutSellerRepository.getReferenceById(sellerId);
            PayoutUser   buyer  = payoutUserRepository.getReferenceById(buyerId);

            BigDecimal orderPrice  = BigDecimal.valueOf(10_000L + (i % 90_001)); // 10,000 ~ 100,000
            BigDecimal fee         = orderPrice.multiply(FEE_RATE).setScale(0, RoundingMode.HALF_UP);
            BigDecimal donationAmt = fee.multiply(DONATION_RATE).setScale(0, RoundingMode.HALF_UP);
            BigDecimal adjustedFee = fee.subtract(donationAmt);
            BigDecimal sellerAmt   = orderPrice.subtract(fee);

            String weightGrade       = weightGradeOf(i);
            BigDecimal distance      = BigDecimal.valueOf(10 + (i % 291));
            BigDecimal carbonKg      = distance.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);

            // 정산__상품판매_대금 (판매자)
            batch.add(candidateItem(SellerEventType.정산__상품판매_대금, relId, paymentDate,
                    buyer, seller, sellerAmt, weightGrade, distance, carbonKg));

            // 정산__상품판매_수수료 (플랫폼)
            batch.add(candidateItem(SellerEventType.정산__상품판매_수수료, relId, paymentDate,
                    buyer, system, adjustedFee, weightGrade, distance, BigDecimal.ZERO));

            // 정산__상품판매_기부금 (기부)
            batch.add(candidateItem(SellerEventType.정산__상품판매_기부금, relId, paymentDate,
                    buyer, donation, donationAmt, weightGrade, distance, carbonKg));

            // 300건마다 flush & clear (메모리 절약)
            if (batch.size() >= 300) {
                payoutCandidateItemRepository.saveAll(batch);
                payoutCandidateItemRepository.flush();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            payoutCandidateItemRepository.saveAll(batch);
        }

        long elapsed = System.currentTimeMillis() - startMs;
        log.info("[LoadTestDataInitializer] 완료 - 판매자 {}명, 구매자 {}명, 후보 {}건 생성 ({}ms)",
                SELLER_COUNT, BUYER_COUNT, ORDER_COUNT * 3L, elapsed);
    }

    // ── private helpers ──────────────────────────────────────────────────

    private boolean isAlreadySeeded() {
        return payoutCandidateItemRepository.existsByRelTypeCodeAndRelIdAndEventType(
                "OrderItem", ORDER_REL_ID_START, SellerEventType.정산__상품판매_대금);
    }

    private void createSellerIfAbsent(long sellerId, LocalDateTime now) {
        if (payoutSellerRepository.existsById(sellerId)) return;
        payoutSellerRepository.save(PayoutSeller.builder()
                .id(sellerId)
                .userId(sellerId)
                .sellerType(SellerType.BUSINESS)
                .storeName("LT-SELLER-" + sellerId)
                .businessNum("LT-BIZ-" + String.format("%06d", sellerId))
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .status(SellerStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private void createBuyerIfAbsent(long buyerId, LocalDateTime now) {
        if (payoutUserRepository.existsById(buyerId)) return;
        payoutUserRepository.save(PayoutUser.builder()
                .id(buyerId)
                .email("ltbuyer" + buyerId + "@mossy.local")
                .name("ltbuyer" + buyerId)
                .address("Seoul")
                .nickname("ltbuyer" + buyerId)
                .profileImage("https://mossy.local/default-profile.png")
                .createdAt(now)
                .updatedAt(now)
                .status(UserStatus.ACTIVE)
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build());
    }

    private void ensureActivePayout(PayoutSeller payee) {
        payoutRepository.findByPayeeAndPayoutDateIsNull(payee)
                .orElseGet(() -> payoutRepository.save(new Payout(payee)));
    }

    private PayoutCandidateItem candidateItem(
            SellerEventType eventType, long relId, LocalDateTime paymentDate,
            PayoutUser payer, PayoutSeller payee,
            BigDecimal amount, String weightGrade,
            BigDecimal deliveryDistance, BigDecimal carbonKg
    ) {
        return PayoutCandidateItem.builder()
                .eventType(eventType)
                .relTypeCode("OrderItem")
                .relId(relId)
                .paymentDate(paymentDate)
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .weightGrade(weightGrade)
                .deliveryDistance(deliveryDistance)
                .carbonKg(carbonKg)
                .build();
    }

    private String weightGradeOf(int i) {
        return switch (i % 4) {
            case 0 -> "소형";
            case 1 -> "중소형";
            case 2 -> "중형";
            default -> "대형";
        };
    }
}
