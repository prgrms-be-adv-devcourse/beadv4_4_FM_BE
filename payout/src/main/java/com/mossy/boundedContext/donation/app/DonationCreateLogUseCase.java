package com.mossy.boundedContext.donation.app;

import com.mossy.boundedContext.payout.domain.calculator.DonationCalculator;
import com.mossy.boundedContext.donation.domain.DonationLog;
import com.mossy.boundedContext.donation.out.DonationLogRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.app.PayoutSupport;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;

import com.mossy.shared.market.payload.OrderPayoutDto;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * [UseCase] 기부 로그 생성을 담당하는 서비스 클래스
 * DonationFacade의 '1단계: 기부 로그 생성' 흐름에서 호출
 */
@Service
@RequiredArgsConstructor
public class DonationCreateLogUseCase {

    private final DonationCalculator donationCalculator;
    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;

    /**
     * 특정 주문 아이템에 대한 기부 로그(DonationLog)를 생성하고 저장
     *
     * @param orderItem 기부금이 발생한 특정 주문 아이템 DTO
     */
    @Transactional
    public void createDonationLog(OrderPayoutDto orderItem) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        // 1. 기부자(구매자) 정보를 조회
        PayoutUser user = payoutSupport.findUserById(orderItem.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_USER_NOT_FOUND));

        // 2. 기부금액을 계산
        BigDecimal donationAmount = donationCalculator.calculate(orderItem);
        if (donationAmount == null || donationAmount.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }

        // 3. 탄소 배출량을 kg 단위로 계산
        BigDecimal carbonKg = donationCalculator.getCarbon(orderItem);
        if (carbonKg == null || carbonKg.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_AMOUNT);
        }

        // 4. 계산된 탄소 배출량을 kg에서 g으로 변환
        Double carbonG = carbonKg.multiply(new BigDecimal("1000")).doubleValue();

        // 5. 계산된 정보를 바탕으로 DonationLog 엔티티를 생성하고 저장
        DonationLog donationLog = DonationLog.builder()
                .user(user)
                .orderItemId(orderItem.id())
                .amount(donationAmount)
                .carbonOffsetG(carbonG)
                .build();

        donationLogRepository.save(donationLog);
    }

    /**
     * 정산 완료 시점에 PayoutCandidateItem의 정보를 사용하여 기부 로그를 생성
     * 이미 계산된 기부금액과 탄소 배출량 정보를 활용하여 중복 계산을 방지
     *
     * @param orderItemId      주문 항목 ID
     * @param buyerId          구매자 ID
     * @param donationAmount   이미 계산된 기부금액
     * @param weightGrade      무게 등급
     * @param deliveryDistance 배송 거리
     */
    @Transactional
    public void createDonationLogDirect(Long orderItemId, Long buyerId, BigDecimal donationAmount,
                                        String weightGrade, BigDecimal deliveryDistance) {
        if (orderItemId == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        if (donationAmount == null || donationAmount.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }

        // 1. 기부자(구매자) 정보를 조회
        PayoutUser user = payoutSupport.findUserById(buyerId)
                .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_USER_NOT_FOUND));

        // 2. 탄소 배출량을 kg 단위로 계산 (OrderPayoutDto 임시 생성)
        OrderPayoutDto tempOrderItem = new OrderPayoutDto(
                orderItemId, null, buyerId, null, null, null,
                null, weightGrade, deliveryDistance, null, null
        );
        BigDecimal carbonKg = donationCalculator.getCarbon(tempOrderItem);
        if (carbonKg == null || carbonKg.signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_AMOUNT);
        }

        // 3. 계산된 탄소 배출량을 kg에서 g으로 변환
        Double carbonG = carbonKg.multiply(new BigDecimal("1000")).doubleValue();

        // 4. DonationLog 엔티티를 생성하고 저장
        DonationLog donationLog = DonationLog.builder()
                .user(user)
                .orderItemId(orderItemId)
                .amount(donationAmount)
                .carbonOffsetG(carbonG)
                .build();

        donationLogRepository.save(donationLog);
    }
}
