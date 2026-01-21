package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기부 Facade
 */
@Service
@RequiredArgsConstructor
public class DonationFacade {

    private final DonationCreateLogUseCase donationCreateLogUseCase;
    private final DonationSettleUseCase donationSettleUseCase;

    /**
     * 주문 아이템에 대한 기부 로그 생성
     * @param order 주문 정보
     * @param orderItem 주문 아이템 정보
     */
    @Transactional
    public void createDonationLog(OrderDto order, OrderItemDto orderItem) {
        donationCreateLogUseCase.createDonationLog(order, orderItem);
    }

    /**
     * 정산 완료 시 기부 로그를 정산 완료 상태로 변경
     * @param payoutId 완료된 Payout ID
     */
    @Transactional
    public void settleDonationLogs(Long payoutId) {
        donationSettleUseCase.settleDonationLogs(payoutId);
    }
}
