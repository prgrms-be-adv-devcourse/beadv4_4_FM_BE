package backend.mossy.shared.market.event;

import backend.mossy.shared.market.dto.event.OrderDetailDto;
import backend.mossy.shared.market.dto.event.PaymentOrderDto;

import java.util.List;

public record PaymentCompletedEvent(
        PaymentOrderDto order,
        List<OrderDetailDto> orderDetails   // 주문 상세 : 단일 상품
){ }
