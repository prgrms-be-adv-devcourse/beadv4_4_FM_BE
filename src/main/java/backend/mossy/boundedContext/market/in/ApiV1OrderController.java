package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.order.OrderFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.dto.response.OrderDetailResponse;
import backend.mossy.shared.market.dto.response.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class ApiV1OrderController {
    private final OrderFacade orderFacade;

    @PostMapping
    public RsData<OrderCreatedResponse> createOrder(
            @RequestParam Long userId,
            @RequestBody OrderCreatedRequest request
    ) {
        return new RsData<>("200", "주문이 생성되었습니다.", orderFacade.createOrder(userId, request));
    }

    @GetMapping
    public Page<OrderListResponse> getMyOrders(
            @RequestParam Long userId,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return orderFacade.getOrderListByUserId(userId, pageable);
    }

    @GetMapping("/{orderId}")
    public List<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        return orderFacade.getOrderDetails(orderId);
    }

    @DeleteMapping("/{orderId}")
    public RsData<Void> deleteOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId
    ) {
        orderFacade.deleteOrder(orderId, userId);
        return new RsData<>("200", "주문 삭제를 성공했습니다.");
    }
}
