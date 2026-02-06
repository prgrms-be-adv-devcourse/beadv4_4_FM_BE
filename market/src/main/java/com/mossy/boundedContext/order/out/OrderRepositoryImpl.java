package com.mossy.boundedContext.order.out;

import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailSellerResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.shared.market.enums.OrderState;
import com.mossy.shared.market.payload.OrderPayoutDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Supplier;

import static com.mossy.boundedContext.marketUser.domain.QMarketSeller.marketSeller;
import static com.mossy.boundedContext.marketUser.domain.QMarketUser.marketUser;
import static com.mossy.boundedContext.order.domain.QOrder.order;
import static com.mossy.boundedContext.order.domain.QOrderItem.orderItem;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderPayoutDto> findPayoutOrderByOrderId(Long orderId) {
        return queryFactory
                .select(Projections.constructor(OrderPayoutDto.class,
                        orderItem.id,
                        order.id,
                        marketUser.id,
                        marketUser.name,
                        orderItem.seller.id,
                        orderItem.productId,
                        orderItem.orderPrice,
                        orderItem.createdAt,
                        orderItem.updatedAt
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                    .join(order.buyer, marketUser)
                .where(
                    order.id.eq(orderId),
                    order.state.eq(OrderState.PAID))
                .fetch();
    }

    @Override
    public Page<OrderListResponse> findOrderListByUserId(Long userId, Pageable pageable) {
        BooleanExpression condition = order.buyer.id.eq(userId);

        List<OrderListResponse> content = queryFactory
                .select(Projections.constructor(OrderListResponse.class,
                        order.id,
                        order.orderNo,
                        order.totalPrice,
                        order.state,
                        orderItem.count(),
                        order.address,
                        order.createdAt
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                .where(condition)
                .groupBy(order.id)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return createPage(content, pageable, () -> queryFactory
                .select(order.count())
                .from(order)
                .where(condition)
                .fetchOne());
    }

    @Override
    public List<OrderDetailResponse> findOrderDetailsByOrderId(Long orderId) {
        return queryFactory
                .select(Projections.constructor(OrderDetailResponse.class,
                        orderItem.productId,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        marketSeller.storeName
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                    .join(orderItem.seller, marketSeller)
                .where(order.id.eq(orderId))
                .fetch();
    }

    @Override
    public Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, Pageable pageable) {
        BooleanExpression condition = orderItem.seller.id.eq(sellerId);

        List<OrderListSellerResponse> content = queryFactory
                .select(Projections.constructor(OrderListSellerResponse.class,
                        orderItem.id,
                        orderItem.productId,
                        orderItem.quantity,
                        orderItem.orderPrice,
                        order.state,
                        orderItem.createdAt
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                .where(condition)
                .orderBy(orderItem.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return createPage(content, pageable, () -> queryFactory
                .select(orderItem.count())
                .from(orderItem)
                .where(condition)
                .fetchOne());
    }

    @Override
    public OrderDetailSellerResponse findSellerOrderDetailById(Long orderItemId) {
        return queryFactory
                .select(Projections.constructor(OrderDetailSellerResponse.class,
                        order.orderNo,
                        marketUser.name,
                        order.address
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                    .join(order.buyer, marketUser)
                .where(orderItem.id.eq(orderItemId))
                .fetchOne();
    }

    private <T> Page<T> createPage(List<T> content, Pageable pageable, Supplier<Long> countSupplier) {
        if (content.size() < pageable.getPageSize()) {
            long total = pageable.getOffset() + content.size();
            return new PageImpl<>(content, pageable, total);
        }
        Long total = countSupplier.get();
        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
