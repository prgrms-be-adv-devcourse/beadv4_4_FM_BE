package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.event.OrderCreateDto;

public record OrderCreatedEvent (
        OrderCreateDto orderDto
) { }
