package backend.mossy.boundedContext.market.in.order;

import backend.mossy.boundedContext.auth.infra.security.UserDetailsImpl;
import backend.mossy.boundedContext.market.app.order.OrderFacade;
import backend.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import backend.mossy.shared.market.dto.response.OrderListSellerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Page<OrderListSellerResponse> getSellerOrders(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @Parameter(hidden = true)
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Long sellerId = userDetails.getSellerId();
        return orderFacade.getSellerOrderList(sellerId, pageable);
    }

    @Operation(
            summary = "판매자 판매 내역 상세 조회",
            description = "판매자가 판매한 특정 상품의 판매 내역의 상세를 조회합니다."
    )
    @GetMapping("/{orderDetailId}")
    public OrderDetailSellerResponse getSellerOrderDetail(
            @Parameter(description = "주문 상세 ID", required = true)
            @PathVariable Long orderDetailId
    ) {
        return orderFacade.getSellerOrderDetail(orderDetailId);
    }
}