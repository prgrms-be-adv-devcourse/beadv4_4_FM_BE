package backend.mossy.boundedContext.payout.in.donation;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.shared.payout.event.PayoutCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * [Inbound Adapter] 정산(Payout) 완료 이벤트를 수신하여 기부(Donation) 관련 후속 처리를 담당하는 리스너 클래스
 * Payout 컨텍스트에서 PayoutCompletedEvent가 발행되면, 이 리스너가 해당 이벤트를 감지하여
 * 기부금 정산 로직을 트리거
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DonationEventListener {

    private final DonationFacade donationFacade;

    /**
     * PayoutCompletedEvent를 수신하여 기부 로그를 정산 완료 상태로 처리
     * 이 메서드는 Payout 트랜잭션이 성공적으로 커밋된 후에 호출 (TransactionPhase.AFTER_COMMIT).
     *
     * @param event 정산 완료 이벤트 객체 (PayoutCompletedEvent)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPayoutCompleted(PayoutCompletedEvent event) {
        log.info("정산 완료 이벤트(Payout ID: {})에 대한 후원 정산 처리를 시작합니다.", event.payout().id());

        // DonationFacade를 통해 해당 Payout ID와 연관된 기부 로그들을 정산 완료 상태로 변경
        donationFacade.settleDonationLogs(event.payout().id());

        log.info("후원 기록 정산이 완료되었습니다(Payout ID: {}).", event.payout().id());
    }
}
