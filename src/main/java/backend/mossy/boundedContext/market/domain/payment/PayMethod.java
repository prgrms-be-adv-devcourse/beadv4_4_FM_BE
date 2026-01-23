package backend.mossy.boundedContext.market.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayMethod {
    CARD("신용/체크카드"),
    CASH("예치금");

    private final String description;
}
