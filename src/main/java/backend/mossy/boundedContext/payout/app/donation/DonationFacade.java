package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void createDonationLog(OrderItemDto orderItem) {
        donationCreateLogUseCase.createDonationLog(orderItem);
    }

    /**
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
}
