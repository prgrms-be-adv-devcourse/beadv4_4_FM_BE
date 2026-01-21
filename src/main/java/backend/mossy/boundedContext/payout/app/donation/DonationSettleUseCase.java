package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 정산 완료 시 기부 로그를 정산 완료 상태로 변경하는 UseCase
 */
@Service
@RequiredArgsConstructor
public class DonationSettleUseCase {

    private final PayoutRepository payoutRepository;
    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;

    /**
     * Payout 완료 시 해당 기부 로그들을 정산 완료 처리
     * @param payoutId 완료된 Payout ID
     */
    @Transactional
    public void settleDonationLogs(Long payoutId) {
        // 1. Payout 조회
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new IllegalArgumentException("Payout not found: " + payoutId));

        // 2. DONATION seller 조회
        PayoutSeller donationSeller = payoutSupport.findDonationSeller()
                .orElseThrow(() -> new IllegalStateException("DONATION seller not found"));

        // 3. Payout에서 DONATION payee인 PayoutItem들의 orderItemId 추출
        List<Long> orderItemIds = payout.getItems().stream()
                .filter(item -> item.getPayee().getId().equals(donationSeller.getId()))
                .map(PayoutItem::getRelId)  // relId = orderItemId
                .toList();

        // 4. 해당 orderItemId들의 DonationLog를 정산 완료 처리
        orderItemIds.forEach(orderItemId -> {
            List<DonationLog> donationLogs = donationLogRepository.findByOrderItemId(orderItemId);
            donationLogs.forEach(DonationLog::settle);
        });
    }
}
