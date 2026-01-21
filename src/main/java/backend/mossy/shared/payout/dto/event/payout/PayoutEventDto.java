package backend.mossy.shared.payout.dto.event.payout;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutEventDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long payeeId,
        String payeeNickname,
        LocalDateTime payoutDate,
        BigDecimal amount,
        boolean isSystem
) {

}
