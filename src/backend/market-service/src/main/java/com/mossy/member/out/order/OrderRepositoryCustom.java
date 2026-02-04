package com.mossy.member.out.order;

import com.mossy.shared.market.dto.event.OrderPayoutDto;
import com.mossy.shared.market.dto.response.OrderDetailResponse;
import com.mossy.shared.market.dto.response.OrderListResponse;
import com.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import com.mossy.shared.market.dto.response.OrderListSellerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {

    List<OrderPayoutDto> findPayoutOrderByOrderId(Long orderId);

    Page<OrderListResponse> findOrderListByUserId(Long userId, Pageable pageable);

    List<OrderDetailResponse> findOrderDetailsByOrderId(Long orderId);

    Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, Pageable pageable);

    OrderDetailSellerResponse findSellerOrderDetailById(Long orderDetailId);
}