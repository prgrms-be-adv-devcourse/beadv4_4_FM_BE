package backend.mossy.boundedContext.payout.domain.event;

import backend.mossy.shared.payout.dto.response.PayoutSellerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayoutSellerCreatedEvent {
    private PayoutSellerResponseDto payoutSeller;
}
