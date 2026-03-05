package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("!prod")
@Order(1)
@RequiredArgsConstructor
public class PayoutDataInitializer implements CommandLineRunner {

    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutUserRepository payoutUserRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutRepository payoutRepository;

    @Override
    @Transactional
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();

        // reserved seller ids
        createReservedSellerIfAbsent(1L, 1L, "SYSTEM", "PAYOUT-SYSTEM-0001", now);
        createReservedSellerIfAbsent(2L, 2L, "HOLDING", "PAYOUT-HOLDING-0002", now);
        createReservedSellerIfAbsent(3L, 3L, "DONATION", "PAYOUT-DONATION-0003", now);

        // quick manual test sample data
        createUserIfAbsent(101L, "buyer101@mossy.local", "buyer101", now);
        createUserIfAbsent(201L, "seller201@mossy.local", "seller201", now);
        createSellerIfAbsent(201L, 201L, "SELLER201", "PAYOUT-SELLER-0201", now);

        // bulk sample actors for 100-case payout test
        for (long userId = 102L; userId <= 120L; userId++) {
            createUserIfAbsent(userId, "buyer" + userId + "@mossy.local", "buyer" + userId, now);
        }
        createSellerIfAbsent(211L, 211L, "SELLER211", "PAYOUT-SELLER-0211", now);
        createSellerIfAbsent(212L, 212L, "SELLER212", "PAYOUT-SELLER-0212", now);
        createSellerIfAbsent(213L, 213L, "SELLER213", "PAYOUT-SELLER-0213", now);
        createSellerIfAbsent(214L, 214L, "SELLER214", "PAYOUT-SELLER-0214", now);

        // payout sample data (candidate + payout history)
        seedPayoutSamples(now);

        validateReservedSeller(1L, "SYSTEM");
        validateReservedSeller(2L, "HOLDING");
        validateReservedSeller(3L, "DONATION");

        log.info("[PayoutDataInitializer] reserved sellers(1,2,3), users, 100-case candidates, payout history are ready");
    }

    private void createReservedSellerIfAbsent(
            Long sellerId,
            Long userId,
            String storeName,
            String businessNum,
            LocalDateTime now
    ) {
        payoutSellerRepository.findById(sellerId).ifPresentOrElse(existing -> {
            if (!storeName.equals(existing.getStoreName())) {
                throw new IllegalStateException(
                        "Reserved payout seller id=%d must be %s but was %s"
                                .formatted(sellerId, storeName, existing.getStoreName())
                );
            }
        }, () -> payoutSellerRepository.save(PayoutSeller.builder()
                .id(sellerId)
                .userId(userId)
                .sellerType(SellerType.BUSINESS)
                .storeName(storeName)
                .businessNum(businessNum)
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .status(SellerStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build()));
    }

    private void createSellerIfAbsent(
            Long sellerId,
            Long userId,
            String storeName,
            String businessNum,
            LocalDateTime now
    ) {
        if (payoutSellerRepository.existsById(sellerId)) return;

        payoutSellerRepository.save(PayoutSeller.builder()
                .id(sellerId)
                .userId(userId)
                .sellerType(SellerType.BUSINESS)
                .storeName(storeName)
                .businessNum(businessNum)
                .latitude(new BigDecimal("37.5665000"))
                .longitude(new BigDecimal("126.9780000"))
                .status(SellerStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

    private void createUserIfAbsent(Long userId, String email, String nickname, LocalDateTime now) {
        if (payoutUserRepository.existsById(userId)) return;

        payoutUserRepository.save(PayoutUser.builder()
                .id(userId)
                .email(email)
                .name(nickname)
                .address("Seoul")
                .nickname(nickname)
                .profileImage("https://mossy.local/default-profile.png")
                .createdAt(now)
                .updatedAt(now)
                .status(UserStatus.ACTIVE)
                .latitude(new BigDecimal("37.5665000"))
                .longitude(new BigDecimal("126.9780000"))
                .build());
    }

    private void validateReservedSeller(Long sellerId, String expectedStoreName) {
        PayoutSeller seller = payoutSellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalStateException("Reserved payout seller id=%d is missing".formatted(sellerId)));

        if (!expectedStoreName.equals(seller.getStoreName())) {
            throw new IllegalStateException(
                    "Reserved payout seller id=%d must be %s but was %s"
                            .formatted(sellerId, expectedStoreName, seller.getStoreName())
            );
        }
    }

    private void seedPayoutSamples(LocalDateTime now) {
        PayoutUser buyer = payoutUserRepository.findById(101L)
                .orElseThrow(() -> new IllegalStateException("sample buyer(101) is missing"));
        PayoutSeller seller = payoutSellerRepository.findById(201L)
                .orElseThrow(() -> new IllegalStateException("sample seller(201) is missing"));
        PayoutSeller system = payoutSellerRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("system seller(1) is missing"));
        PayoutSeller donation = payoutSellerRepository.findById(3L)
                .orElseThrow(() -> new IllegalStateException("donation seller(3) is missing"));
        PayoutSeller holding = payoutSellerRepository.findById(2L)
                .orElseThrow(() -> new IllegalStateException("holding seller(2) is missing"));

        // ---- candidate samples ----
        createCandidateIfAbsent(
                99001L,
                SellerEventType.정산__상품판매_대금,
                buyer,
                seller,
                new BigDecimal("8000"),
                now.minusDays(10),
                "중형",
                new BigDecimal("32"),
                new BigDecimal("1.20")
        );
        createCandidateIfAbsent(
                99001L,
                SellerEventType.정산__상품판매_수수료,
                buyer,
                system,
                new BigDecimal("1500"),
                now.minusDays(10),
                "중형",
                new BigDecimal("32"),
                BigDecimal.ZERO
        );
        createCandidateIfAbsent(
                99001L,
                SellerEventType.정산__상품판매_기부금,
                buyer,
                donation,
                new BigDecimal("500"),
                now.minusDays(10),
                "중형",
                new BigDecimal("32"),
                new BigDecimal("1.20")
        );

        createCandidateIfAbsent(
                99001L,
                SellerEventType.임시보관__주문결제,
                buyer,
                holding,
                new BigDecimal("10000"),
                now.minusDays(10),
                "중형",
                new BigDecimal("32"),
                BigDecimal.ZERO
        );

        // bulk candidates의 각 payee에 대한 활성 Payout 생성 (collectPayoutItemsMore 전제 조건)
        ensureActivePayout(system);
        ensureActivePayout(holding);
        ensureActivePayout(donation);
        ensureActivePayout(seller);
        ensureActivePayout(payoutSellerRepository.findById(211L).orElseThrow());
        ensureActivePayout(payoutSellerRepository.findById(212L).orElseThrow());
        ensureActivePayout(payoutSellerRepository.findById(213L).orElseThrow());
        ensureActivePayout(payoutSellerRepository.findById(214L).orElseThrow());

        seedBulkPayoutCandidates(now, system, donation);

        // ---- payout history samples ----
        // completed + credited payout: 화면에서 "정산 완료/지급 완료" 확인용
        Payout done = ensurePayoutItem(
                seller,
                buyer,
                98002L,
                SellerEventType.정산__상품판매_대금,
                now.minusDays(15),
                new BigDecimal("9100")
        );
        if (!done.isCompleted()) done.completePayout();
        if (!done.isCredited()) done.creditToWallet();

        // active payout: 화면에서 "정산 대기/진행 중" 확인용
        ensurePayoutItem(
                seller,
                buyer,
                98001L,
                SellerEventType.정산__상품판매_대금,
                now.minusDays(2),
                new BigDecimal("12000")
        );
    }

    private void ensureActivePayout(PayoutSeller payee) {
        payoutRepository.findByPayeeAndPayoutDateIsNull(payee)
                .orElseGet(() -> payoutRepository.save(new Payout(payee)));
    }

    private void seedBulkPayoutCandidates(LocalDateTime now, PayoutSeller system, PayoutSeller donation) {
        List<Long> sellerIds = List.of(201L, 211L, 212L, 213L, 214L);

        for (int i = 1; i <= 100; i++) {
            long relId = 700000L + i;
            long buyerId = 101L + ((i - 1) % 20);
            long sellerId = sellerIds.get((i - 1) % sellerIds.size());

            PayoutUser buyer = payoutUserRepository.findById(buyerId)
                    .orElseThrow(() -> new IllegalStateException("sample buyer(" + buyerId + ") is missing"));
            PayoutSeller seller = payoutSellerRepository.findById(sellerId)
                    .orElseThrow(() -> new IllegalStateException("sample seller(" + sellerId + ") is missing"));

            BigDecimal finalPrice = BigDecimal.valueOf(10000L + (i * 100L)); // 10,100 ~ 20,000
            BigDecimal fee = finalPrice.multiply(new BigDecimal("0.20")).setScale(0, java.math.RoundingMode.HALF_UP);
            BigDecimal donationAmount = fee.multiply(new BigDecimal("0.25")).setScale(0, java.math.RoundingMode.HALF_UP);
            BigDecimal adjustedFee = fee.subtract(donationAmount);
            BigDecimal sellerAmount = finalPrice.subtract(fee);
            BigDecimal platformDiscount = (i % 5 == 0) ? new BigDecimal("300") : BigDecimal.ZERO;

            LocalDateTime paymentDate = now.minusDays(12 + (i % 7));
            String weightGrade = (i % 3 == 0) ? "중형" : ((i % 3 == 1) ? "소형" : "중소형");
            BigDecimal deliveryDistance = BigDecimal.valueOf(20 + (i % 40));
            BigDecimal carbonKg = BigDecimal.valueOf(0.30 + (i % 8) * 0.10).setScale(2, java.math.RoundingMode.HALF_UP);

            createCandidateIfAbsent(relId, SellerEventType.정산__상품판매_대금, buyer, seller, sellerAmount, paymentDate, weightGrade, deliveryDistance, carbonKg);
            createCandidateIfAbsent(relId, SellerEventType.정산__상품판매_수수료, buyer, system, adjustedFee, paymentDate, weightGrade, deliveryDistance, BigDecimal.ZERO);
            createCandidateIfAbsent(relId, SellerEventType.정산__상품판매_기부금, buyer, donation, donationAmount, paymentDate, weightGrade, deliveryDistance, carbonKg);

            if (platformDiscount.compareTo(BigDecimal.ZERO) > 0) {
                createCandidateIfAbsent(relId, SellerEventType.정산__프로모션_플랫폼부담, null, seller, platformDiscount, paymentDate, weightGrade, deliveryDistance, BigDecimal.ZERO);
            }

            // every 4th order: partial refund samples
            if (i % 4 == 0) {
                BigDecimal refundRate = new BigDecimal("0.50");
                createCandidateIfAbsent(relId, SellerEventType.정산__상품환불_대금, buyer, seller, sellerAmount.multiply(refundRate).setScale(0, java.math.RoundingMode.DOWN).negate(), now.minusDays(3), weightGrade, deliveryDistance, carbonKg.negate());
                createCandidateIfAbsent(relId, SellerEventType.정산__상품환불_수수료, buyer, system, adjustedFee.multiply(refundRate).setScale(0, java.math.RoundingMode.DOWN).negate(), now.minusDays(3), weightGrade, deliveryDistance, BigDecimal.ZERO);
                createCandidateIfAbsent(relId, SellerEventType.정산__상품환불_기부금, buyer, donation, donationAmount.multiply(refundRate).setScale(0, java.math.RoundingMode.DOWN).negate(), now.minusDays(3), weightGrade, deliveryDistance, carbonKg.multiply(refundRate).setScale(2, java.math.RoundingMode.HALF_UP).negate());
                if (platformDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    createCandidateIfAbsent(relId, SellerEventType.정산__상품환불_플랫폼부담, null, seller, platformDiscount.multiply(refundRate).setScale(0, java.math.RoundingMode.DOWN).negate(), now.minusDays(3), weightGrade, deliveryDistance, BigDecimal.ZERO);
                }
            }
        }
    }

    private void createCandidateIfAbsent(
            Long relId,
            SellerEventType eventType,
            PayoutUser payer,
            PayoutSeller payee,
            BigDecimal amount,
            LocalDateTime paymentDate,
            String weightGrade,
            BigDecimal deliveryDistance,
            BigDecimal carbonKg
    ) {
        boolean exists = payoutCandidateItemRepository.existsByRelTypeCodeAndRelIdAndEventType("OrderItem", relId, eventType);
        if (exists) return;

        payoutCandidateItemRepository.save(PayoutCandidateItem.builder()
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
                .build());
    }

    private Payout ensurePayoutItem(
            PayoutSeller payee,
            PayoutUser payer,
            Long relId,
            SellerEventType eventType,
            LocalDateTime paymentDate,
            BigDecimal amount
    ) {
        Payout existingPayout = payoutRepository.findAll().stream()
                .filter(p -> p.getPayee() != null && payee.getId().equals(p.getPayee().getId()))
                .filter(p -> p.getItems().stream().anyMatch(item ->
                        relId.equals(item.getRelId()) && eventType == item.getEventType()))
                .findFirst()
                .orElse(null);
        if (existingPayout != null) {
            return existingPayout;
        }

        Payout payout = payoutRepository.findByPayeeAndPayoutDateIsNull(payee)
                .orElseGet(() -> payoutRepository.save(new Payout(payee)));
        payout.addItem(eventType, "OrderItem", relId, paymentDate, payer, payee, amount);

        return payout;
    }
}
