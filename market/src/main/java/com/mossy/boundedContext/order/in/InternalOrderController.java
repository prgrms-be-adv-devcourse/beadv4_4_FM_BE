package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderInternalQueryUseCase;
import com.mossy.boundedContext.order.in.dto.response.MarketOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Internal Order API", description = "서비스 간 통신용 주문 조회 API")
@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderInternalQueryUseCase orderInternalQueryUseCase;

    @Operation(
        summary = "주문 ID로 주문 조회 (Internal)",
        description = "서비스 간 통신용 - 주문 ID로 주문 정보를 조회합니다."
    )
    @GetMapping("/{orderId}")
    public MarketOrderResponse getOrder(@PathVariable("orderId") Long orderId) {
        return orderInternalQueryUseCase.getOrderById(orderId);
    }

    @Operation(
        summary = "주문 번호로 주문 조회 (Internal)",
        description = "서비스 간 통신용 - 주문 번호(orderNo)로 주문 정보를 조회합니다."
    )
    @GetMapping("/by-order-no/{orderNo}")
    public MarketOrderResponse getOrderByOrderNo(@PathVariable("orderNo") String orderNo) {
        return orderInternalQueryUseCase.getOrderByOrderNo(orderNo);
    }
}
