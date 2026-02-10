package com.mossy.boundedContext.donation.app;

import com.mossy.shared.market.payload.OrderPayoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 기부(Donation) 기능의 메인 진입점 역할을 하는 파사드(Facade)
 * 기부와 관련된 핵심 비즈니스 로직(Use Case)들을 외부 또는 내부 모듈에서 쉽게 사용할 수 있도록 캡슐화
 * <p>
 * 기부 플로우:
 * 1. 기부금 발생 (예: 특정 상품 주문) -> createDonationLog 호출
 * 2. 정산 완료 -> settleDonationLogs 호출
 */
@Service
@RequiredArgsConstructor
public class DonationFacade {

    private final DonationCreateLogUseCase donationCreateLogUseCase;
    private final DonationSettleUseCase donationSettleUseCase;

    /**
     * [1단계: 기부 로그 생성]
     * 주문 아이템 정보를 기반으로 기부 로그(DonationLog)를 생성
     * 이 메서드는 주로 결제 완료 이벤트 리스너에서 호출되어, 기부금이 발생했음을 기록하는 역할
     *
     * @param orderItem 기부금이 발생한 특정 주문 아이템 DTO
     */
     //집계 기능 사용 시 (Payout을 통한 기부금 정산)
     /*
      * [2단계: 기부 로그 정산 처리]
      * 정산(Payout)이 완료되었을 때, 해당 정산에 포함된 기부 로그들을 '정산 완료' 상태로 변경
      * 이 메서드는 PayoutCompletedEvent를 처리하는 DonationEventListener에 의해 호출
      *
      * @param payoutId 정산이 완료된 Payout의 ID
      */
     @Transactional
     public void settleDonationLogs(Long payoutId) {
         donationSettleUseCase.settleDonationLogs(payoutId);
     }

    // 집계 없이 정산만 사용 시
    @Transactional
    public void createDonationLog(OrderPayoutDto orderItem) {
        donationCreateLogUseCase.createDonationLog(orderItem);
    }

    /**
     * [1단계: 기부 로그 생성 - 정산 완료 시]
     * PayoutCandidateItem의 정보를 사용하여 기부 로그를 직접 생성
     * 이미 계산된 기부금액과 탄소 배출량 정보를 활용
     *
     * @param orderItemId      주문 항목 ID
     * @param buyerId          구매자 ID
     * @param donationAmount   이미 계산된 기부금액
     * @param weightGrade      무게 등급
     * @param deliveryDistance 배송 거리
     */
    @Transactional
    public void createDonationLogDirect(Long orderItemId, Long buyerId, java.math.BigDecimal donationAmount,
                                        String weightGrade, java.math.BigDecimal deliveryDistance) {
        donationCreateLogUseCase.createDonationLogDirect(orderItemId, buyerId, donationAmount, weightGrade, deliveryDistance);
    }

    /**
     * [2단계: 기부 로그 정산 처리]
     * 주문 항목 ID 리스트를 기반으로 해당 기부 로그들을 '정산 완료' 상태로 변경
     *
     * @param orderItemIds 정산할 주문 항목 ID 리스트
     */
    @Transactional
    public void settleDonationLogsByOrderItemIds(List<Long> orderItemIds) {
        donationSettleUseCase.settleDonationLogsByOrderItemIds(orderItemIds);
    }
}
