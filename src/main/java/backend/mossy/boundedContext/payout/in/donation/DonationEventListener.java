package backend.mossy.boundedContext.payout.in.donation;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.shared.payout.event.PayoutCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DonationEventListener {

    private final DonationFacade donationFacade;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void PayoutCompleted(PayoutCompletedEvent event) {
        log.info("정산 완료 이벤트(Payout ID: {})에 대한 후원 정산 처리를 시작합니다.", event.payout().id());

        donationFacade.settleDonationLogs(event.payout().id());

        log.info("후원 기록 정산이 완료되었습니다(Payout ID: {}).", event.payout().id());
    }
}
