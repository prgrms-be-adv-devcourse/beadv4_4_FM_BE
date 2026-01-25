package backend.mossy.shared.cash.dto.response;

import backend.mossy.boundedContext.cash.domain.user.UserCashLog;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCashLogResponseDto(
    Long id,
    String eventType,
    BigDecimal amount,
    BigDecimal balance,
    LocalDateTime createdAt
) {
    public static UserCashLogResponseDto from(UserCashLog log) {
        return new UserCashLogResponseDto(
            log.getId(),
            log.getEventType().name(),
            log.getAmount(),
            log.getBalance(),
            log.getCreatedAt()
        );
    }
}
