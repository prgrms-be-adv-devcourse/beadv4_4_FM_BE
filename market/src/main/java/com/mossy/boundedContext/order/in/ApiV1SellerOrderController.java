package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import com.mossy.shared.market.enums.OrderState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

            @RequestParam(required = false) OrderState state,

            @Parameter(hidden = true)
            @PageableDefault Pageable pageable
    ) {
        Page<OrderListSellerResponse> response = orderFacade.getSellerOrderList(sellerId, state, pageable);
        return RsData.success(SuccessCode.SELLER_ORDER_LIST, response);
    }
}
