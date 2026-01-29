package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [UseCase] 기부금을 정산 처리하는 서비스 클래스
 * DonationFacade의 '2단계: 기부 로그 정산 처리' 흐름에서 호출
 * 정산(Payout)이 완료된 후, 해당 정산에 포함된 기부 항목들을 '정산 완료' 상태로 변경
 */
@Service
@RequiredArgsConstructor
public class DonationSettleUseCase {

    private final PayoutRepository payoutRepository;
    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;

    /**
     * 특정 정산(Payout)이 완료되었을 때, 관련된 모든 기부 로그(DonationLog)를 '정산 완료' 상태로 업데이트
     *
     * @param payoutId 완료된 Payout의 ID
     */
    @Transactional
    public void settleDonationLogs(Long payoutId) {
        if (payoutId == null) {
            throw new DomainException(ErrorCode.PAYOUT_NOT_FOUND);
        }
        // 1. 정산 완료된 Payout 객체를 조회
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_NOT_FOUND));

        // 2. 시스템에 정의된 '기부금 수령 판매자(DONATION seller)' 정보를 조회
        //    기부금은 이 가상의 판매자에게 지급되는 형식으로 처리
        PayoutSeller donationSeller = payoutSupport.findDonationSeller()
                .orElseThrow(() -> new DomainException(ErrorCode.DONATION_SELLER_NOT_FOUND));

        // 3. 완료된 Payout에 포함된 항목(PayoutItem)들 중에서,
        //    수령인이 '기부금 수령 판매자'인 항목들만 필터링
        //    그 후, 해당 항목들의 'relId' (여기서는 orderItemId에 해당)를 추출
        List<Long> orderItemIds = payout.getItems().stream()
                .filter(item -> item.getPayee().getId().equals(donationSeller.getId()))
                .map(PayoutItem::getRelId)  // relId는 PayoutItem의 관련 ID이며, 기부 항목의 경우 orderItemId를 저장
                .toList();
        if (orderItemIds.isEmpty()) {
            throw new DomainException(ErrorCode.DONATION_PAYOUT_ITEM_NOT_FOUND);
        }

        // 4. 추출된 orderItemId 목록을 순회하면서, 각 ID에 해당하는 DonationLog를 찾아 '정산 완료' 처리
        //    DonationLog의 settle() 메서드는 해당 로그의 상태를 변경
        orderItemIds.forEach(orderItemId -> {
            List<DonationLog> donationLogs = donationLogRepository.findByOrderItemId(orderItemId);
            if (donationLogs.isEmpty()) {
                throw new DomainException(ErrorCode.DONATION_LOG_NOT_FOUND);
            }
            donationLogs.forEach(DonationLog::settle);
        });
    }
}
