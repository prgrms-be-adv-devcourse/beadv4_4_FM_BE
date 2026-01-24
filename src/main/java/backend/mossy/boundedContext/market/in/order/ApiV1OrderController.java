package backend.mossy.boundedContext.market.in.order;

import backend.mossy.boundedContext.auth.infra.security.UserDetailsImpl;
import backend.mossy.boundedContext.market.app.order.OrderFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.dto.response.OrderDetailResponse;
import backend.mossy.shared.market.dto.response.OrderListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "주문 생성 및 구매 내역 API")
public class ApiV1OrderController {
    private final OrderFacade orderFacade;


    @Operation(
            summary = "주문 생성",
            description = "장바구니 또는 상품 정보를 기반으로 주문을 생성합니다."
    )
    @PostMapping
    public RsData<OrderCreatedResponse> createOrder(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @Parameter(description = "주문 생성 요청 DTO", required = true)
            @RequestBody OrderCreatedRequest request
    ) {
        Long userId = userDetails.getUserId();
        return new RsData<>("200", "주문이 생성되었습니다.", orderFacade.createOrder(userId, request));
    }

    @Operation(
            summary = "구매 내역 목록 조회",
            description = "사용자의 구매 내역 목록을 페이징하여 조회합니다."
    )
    @GetMapping
    public Page<OrderListResponse> getMyOrders(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails,

            @Parameter(hidden = true)
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Long userId = userDetails.getUserId();
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


    @DeleteMapping("/{orderId}")
    public RsData<Void> deleteOrder(
            @PathVariable Long orderId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderFacade.deleteOrder(orderId, userId);
        return new RsData<>("200", "주문 삭제를 성공했습니다.");
    }
}
