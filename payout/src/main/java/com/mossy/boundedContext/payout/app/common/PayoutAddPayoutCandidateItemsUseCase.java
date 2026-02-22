package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.calculator.DonationCalculator;
import com.mossy.boundedContext.payout.domain.calculator.FeeCalculator;
import com.mossy.boundedContext.payout.domain.calculator.CarbonCalculator;
import com.mossy.boundedContext.payout.app.PayoutSupport;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateItemCreateDto;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;


import com.mossy.shared.payout.enums.PayoutEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * [UseCase] 정산 후보 아이템 생성을 담당하는 서비스 클래스
 * PayoutFacade의 '1단계: 정산 후보 아이템 생성' 흐름에서 호출
 * 결제가 완료되었을 때, 해당 주문 아이템 정보를 바탕으로 미래에 정산될 항목들을 미리 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final DonationCalculator donationCalculator;
    private final FeeCalculator feeCalculator;
    private final CarbonCalculator carbonCalculator;
    private final PayoutMapper payoutMapper;

    /**
     * 단일 주문 아이템(OrderItem)에 대해 정산 후보 항목을 생성
     * Kafka at-least-once 특성으로 인한 중복 이벤트를 멱등성 체크로 방어
     *
     * @param dto 정산 후보 생성을 위한 DTO (OrderItem 정보 + 계산된 거리/무게등급 포함)
     */
    @Transactional
    public void addPayoutCandidateItem(PayoutCandidateCreateDto dto) {
        if (dto == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        if (dto.paymentDate() == null) {
            throw new DomainException(ErrorCode.PAYMENT_DATE_IS_NULL);
        }

        // [레이어 2] 멱등성 체크: 이미 처리된 이벤트인지 확인
        // 정산__상품판매_대금은 모든 OrderItem 처리 시 반드시 생성되므로 대표 체크값으로 사용
        if (payoutCandidateItemRepository.existsByRelTypeCodeAndRelIdAndEventType(
                "OrderItem", dto.orderItemId(), PayoutEventType.정산__상품판매_대금)) {
            log.warn("[멱등성] 이미 처리된 이벤트 skip. orderItemId={}", dto.orderItemId());
            return;
        }

        try {
            makePayoutCandidateItems(dto);
        } catch (DataIntegrityViolationException e) {
            // [레이어 1] Unique Constraint 위반: 멱등성 체크 직후 동시 요청이 먼저 저장한 경우
            // 중복이므로 정상 종료 처리 (Kafka offset commit 됨 → 재소비 루프 방지)
            log.warn("[멱등성] Unique Constraint 위반으로 skip. orderItemId={}", dto.orderItemId());
        }
    }

    /**
     * 하나의 주문 아이템(OrderItem)을 여러 개의 정산 후보 아이템(PayoutCandidateItem)으로 분해하여 생성
     * 예를 들어, 하나의 상품 판매는 (1)판매자에게 갈 판매대금, (2)플랫폼의 수수료, (3)기부금
     *
     * @param dto 정산 후보 생성을 위한 DTO (OrderItem 정보 + 계산된 거리/무게등급 포함)
     */
    private void makePayoutCandidateItems(
            PayoutCandidateCreateDto dto
    ) {
        // --- 정산에 필요한 주요 주체(Actor)들을 조회 ---
        PayoutSeller system = payoutSupport.findSystemSeller()
                .orElseThrow(() -> new DomainException(ErrorCode.SYSTEM_SELLER_NOT_FOUND)); // 시스템(플랫폼)
        PayoutSeller donation = payoutSupport.findDonationSeller()
                .orElseThrow(() -> new DomainException(ErrorCode.DONATION_SELLER_NOT_FOUND)); // 기부금 수령처(가상 판매자)
        PayoutUser buyer = payoutSupport.findUserById(dto.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.BUYER_NOT_FOUND)); // 구매자
        PayoutSeller seller = payoutSupport.findSellerById(dto.sellerId())
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND)); // 판매자

        // --- 금액을 계산 ---
        // 1. 탄소 배출량 기반으로 수수료를 계산
        BigDecimal payoutFee = feeCalculator.calculate(dto);
        if (payoutFee == null || payoutFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // 2. 이 상품 판매로 인해 발생한 기부금을 계산
        BigDecimal donationAmount = donationCalculator.calculate(dto);
        if (donationAmount == null || donationAmount.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }

        // 2-1. 탄소 배출량 계산 (kg 단위)
        BigDecimal carbonKg = carbonCalculator.calculate(dto);
        if (carbonKg == null || carbonKg.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_AMOUNT);
        }

        // 3. 조정된 수수료를 계산합니다. (원래 수수료 - 기부금)
        BigDecimal adjustedFee = payoutFee.subtract(donationAmount);
        if (adjustedFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // 4. 판매 대금 계산 (주문금액 - 수수료)
        BigDecimal salePriceWithoutFee = dto.orderPrice().subtract(payoutFee);
        if (salePriceWithoutFee.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }

        // --- 계산된 금액을 바탕으로 3가지 종류의 정산 후보 아이템을 생성 ---
        makePayoutCandidateItem(payoutMapper.toCandidateItemCreateDto(
                dto, PayoutEventType.정산__상품판매_수수료, buyer, system, adjustedFee, BigDecimal.ZERO));

        makePayoutCandidateItem(payoutMapper.toCandidateItemCreateDto(
                dto, PayoutEventType.정산__상품판매_대금, buyer, seller, salePriceWithoutFee, BigDecimal.ZERO));

        makePayoutCandidateItem(payoutMapper.toCandidateItemCreateDto(
                dto, PayoutEventType.정산__상품판매_기부금, buyer, donation, donationAmount, carbonKg));

        // 플랫폼 부담 할인이 있으면 판매자 보상 아이템 추가
        BigDecimal platformDiscountAmount = dto.platformDiscountAmount();
        if (platformDiscountAmount != null && platformDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
            makePayoutCandidateItem(payoutMapper.toCandidateItemCreateDto(
                    dto, PayoutEventType.정산__프로모션_플랫폼부담, null, seller, platformDiscountAmount, BigDecimal.ZERO));
        }

    }

    /**
     * 정산 후보 아이템을 생성하고 저장하는 헬퍼 메서드
     */
    private void makePayoutCandidateItem(PayoutCandidateItemCreateDto dto) {
        PayoutCandidateItem payoutCandidateItem = payoutMapper.toEntity(dto);
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}

