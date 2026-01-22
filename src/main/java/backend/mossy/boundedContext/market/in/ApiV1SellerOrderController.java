package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.order.OrderFacade;
import backend.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import backend.mossy.shared.market.dto.response.OrderListSellerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/orders")
@RequiredArgsConstructor
public class ApiV1SellerOrderController {
    private final OrderFacade orderFacade;

    @GetMapping
    public Page<OrderListSellerResponse> getSellerOrders(
            @RequestParam Long userId,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return orderFacade.getSellerOrderList(userId, pageable);
    }

    @GetMapping("/{orderDetailId}")
    public OrderDetailSellerResponse getSellerOrderDetail(@PathVariable Long orderDetailId) {
        return orderFacade.getSellerOrderDetail(orderDetailId);
    }
}