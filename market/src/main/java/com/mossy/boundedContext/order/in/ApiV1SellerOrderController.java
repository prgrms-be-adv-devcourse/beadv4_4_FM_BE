package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailSellerResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
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

@RestController
@RequestMapping("/api/v1/seller/orders")
@RequiredArgsConstructor
@Tag(name = "Seller Order", description = "판매자 판매 내역 API")
public class ApiV1SellerOrderController {

    private final OrderFacade orderFacade;

    @Operation(
            summary = "판매자 판매 내역 목록 조회",
            description = "판매자가 판매한 상품의 판매 내역 목록을 페이징하여 조회합니다."
    )
    @GetMapping
    public RsData<Page<OrderListSellerResponse>> getSellerOrders(
            @Parameter(hidden = true)
            @RequestHeader("X-Seller-Id") Long sellerId,

            @Parameter(hidden = true)
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return RsData.success(SuccessCode.SELLER_ORDER_LIST, orderFacade.getSellerOrderList(sellerId, pageable));
    }

    @Operation(
            summary = "판매자 판매 내역 상세 조회",
            description = "판매자가 판매한 특정 상품의 판매 내역의 상세를 조회합니다."
    )
    @GetMapping("/{orderItemId}")
    public RsData<OrderDetailSellerResponse> getSellerOrderDetail(
            @Parameter(description = "주문 상세 ID", required = true)
            @PathVariable Long orderItemId
    ) {
        return RsData.success(SuccessCode.SELLER_ORDER_DETAIL, orderFacade.getSellerOrderDetail(orderItemId));
    }
}
