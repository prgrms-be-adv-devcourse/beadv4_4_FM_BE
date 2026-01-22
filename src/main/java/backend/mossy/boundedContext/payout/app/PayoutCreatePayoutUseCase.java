package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.Payout;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutRepository;
import backend.mossy.boundedContext.payout.out.PayoutSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutCreatePayoutUseCase {
    private final PayoutRepository payoutRepository;
    private final PayoutSellerRepository payoutSellerRepository;

    public Payout createPayout(Long payeeId) {

        PayoutSeller _payee = payoutSellerRepository.getReferenceById(payeeId);

        Payout payout = payoutRepository.save(
                new Payout(
                        _payee
                )
        );

        return payout;
    }
}
