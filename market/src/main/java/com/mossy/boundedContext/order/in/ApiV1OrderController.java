package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import com.mossy.shared.market.enums.OrderState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "주문 생성 요청 DTO", required = true)
            @RequestBody OrderCreatedRequest request
    ) {
        return RsData.success(SuccessCode.ORDER_CREATE, orderFacade.createOrder(userId, request));
    }

    @Operation(
            summary = "구매 내역 목록 조회",
            description = "사용자의 구매 내역 목록을 페이징하여 조회합니다. 주문 상태와 날짜 범위로 필터링할 수 있습니다."
    )
    @GetMapping
    public RsData<Page<OrderListResponse>> getMyOrders(
            @RequestHeader("X-User-Id") Long userId,

            @Parameter(description = "주문 상태 (PAID: 주문완료, CONFIRMED: 주문확정, CANCELED: 주문취소)")
            @RequestParam(required = false) OrderState state,

            @Parameter(description = "시작 날짜 (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "종료 날짜 (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(hidden = true)
            @PageableDefault Pageable pageable
    ) {
        return RsData.success(SuccessCode.ORDER_LIST, orderFacade.getOrderListByUserId(userId, state, startDate, endDate, pageable));
    }

    @Operation(
            summary = "구매 내역 상세 조회",
            description = "특정 구매 내역의 상세를 조회합니다."
    )
    @GetMapping("/{orderId}")
    public RsData<List<OrderDetailResponse>> getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId
    ) {
        return RsData.success(SuccessCode.ORDER_DETAIL, orderFacade.getOrderDetails(orderId));
    }

    @Operation(summary = "주문 삭제", description = "특정 주문을 삭제 합니다.")
    @DeleteMapping("/{orderId}")
    public RsData<Void> deleteOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId
    ) {
        orderFacade.deleteOrder(orderId, userId);
        return RsData.success(SuccessCode.ORDER_DELETE);
    }
}
