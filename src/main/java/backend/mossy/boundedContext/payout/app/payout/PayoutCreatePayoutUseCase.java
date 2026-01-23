package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase] 특정 수취인(payee)에 대한 Payout(정산) 객체를 생성하는 서비스 클래스
 * PayoutFacade의 '수동 정산 생성' 흐름에서 호출될 수 있으며,
 * 배치 프로세스에 의한 자동 생성과는 별개로 특정 수취인에 대한 정산 객체를 필요할 때 생성하는 역할
 */
@Service
@RequiredArgsConstructor
public class PayoutCreatePayoutUseCase {
    private final PayoutRepository payoutRepository;
    private final PayoutSellerRepository payoutSellerRepository;

    /**
     * 특정 수취인(payee)의 ID를 받아 새로운 Payout(정산) 객체를 생성하거나,
     * 이미 해당 수취인에 대해 활성화된 (아직 정산일이 지정되지 않은) Payout 객체가 있다면 그것을 반환
     *
     * @param payeeId 정산 대상 수취인(PayoutSeller)의 ID
     */
    @Transactional
    public void createPayout(Long payeeId) {
        if (payeeId == null) {
            throw new DomainException(ErrorCode.INVALID_PAYEE_ID);
        }
        // 1. 주어진 payeeId를 사용하여 PayoutSeller 엔티티를 조회
        PayoutSeller _payee = payoutSellerRepository.findById(payeeId)
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

        // 2. 해당 수취인(_payee)에 대해 아직 정산일(payoutDate)이 지정되지 않은(NULL) 활성화된 Payout이 있는지 확인
        //    만약 있다면 기존 Payout을 반환하고, 없다면 새로운 Payout을 생성하여 저장
        payoutRepository.findByPayeeAndPayoutDateIsNull(_payee)
                .orElseGet(() -> payoutRepository.save(new Payout(_payee)));
    }
}
