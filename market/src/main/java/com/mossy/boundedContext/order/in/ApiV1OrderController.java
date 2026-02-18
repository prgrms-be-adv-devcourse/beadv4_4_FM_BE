package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 생성 및 구매 내역 API")
public class ApiV1OrderController {

    private final OrderFacade orderFacade;

    @Operation(
            summary = "주문 생성",
            description = "장바구니 또는 상품 정보를 기반으로 주문을 생성합니다."
    )
    @PostMapping
    public RsData<OrderCreatedResponse> createOrder(
            @RequestParam(name = "userId") Long userId,

            @Parameter(description = "주문 생성 요청 DTO", required = true)
            @RequestBody OrderCreatedRequest request
    ) {
        return RsData.success(SuccessCode.ORDER_CREATE, orderFacade.createOrder(userId, request));
    }

    @Operation(
            summary = "구매 내역 목록 조회",
            description = "사용자의 구매 내역 목록을 페이징하여 조회합니다."
    )
    @GetMapping
    public Page<OrderListResponse> getMyOrders(
            @RequestParam(name = "userId") Long userId,

            @Parameter(hidden = true)
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return orderFacade.getOrderListByUserId(userId, pageable);
    }

    @Operation(
            summary = "구매 내역 상세 조회",
            description = "특정 구매 내역의 상세를 조회합니다."
    )
    @GetMapping("/{orderId}")
    public List<OrderDetailResponse> getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId
    ) {
        return orderFacade.getOrderDetails(orderId);
    }

    @Operation(summary = "주문 삭제", description = "특정 주문을 삭제 합니다.")
    @DeleteMapping("/{orderId}")
    public RsData<Void> deleteOrder(
            @PathVariable Long orderId,
            @RequestParam(name = "userId") Long userId
    ) {
        orderFacade.deleteOrder(orderId, userId);
        return RsData.success(SuccessCode.ORDER_DELETE);
    }

    @Operation(
            summary = "주문 취소",
            description = "결제 완료된 주문을 취소하고 환불을 진행합니다."
    )
    @PostMapping("/{orderId}/cancel")
    public RsData<Void> cancelOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId,

            @RequestParam(name = "userId") Long userId,
            @Parameter(description = "취소 사유", required = true) @RequestParam String cancelReason
    ) {
        orderFacade.cancelOrder(orderId, userId, cancelReason);
        return RsData.success(SuccessCode.ORDER_CANCEL);
    }
}
