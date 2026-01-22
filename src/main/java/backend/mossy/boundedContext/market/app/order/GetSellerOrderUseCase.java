package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import backend.mossy.shared.market.dto.response.OrderListSellerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSellerOrderUseCase {

    private final OrderRepository orderRepository;
    private final MarketSellerRepository marketSellerRepository;

    public Page<OrderListSellerResponse> getSellerOrderList(Long userId, Pageable pageable) {
        MarketSeller seller = marketSellerRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

        return orderRepository.findSellerOrderListBySellerId(seller.getId(), pageable);
    }

    public OrderDetailSellerResponse getSellerOrderDetail(Long orderDetailId) {
        return orderRepository.findSellerOrderDetailById(orderDetailId);
    }
}