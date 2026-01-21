package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutCreatePayoutUseCase {
    private final PayoutRepository payoutRepository;
    private final PayoutSellerRepository payoutSellerRepository;

    public Payout createPayout(Long payeeId) {
        PayoutSeller _payee = payoutSellerRepository.getReferenceById(payeeId);

        // 이미 활성 Payout(payout_date=NULL)이 있으면 기존 것 반환, 없으면 새로 생성
        return payoutRepository.findByPayeeAndPayoutDateIsNull(_payee)
                .orElseGet(() -> payoutRepository.save(new Payout(_payee)));
    }
}
