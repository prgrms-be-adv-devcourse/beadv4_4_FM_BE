package com.mossy.boundedContext.order.out;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.shared.market.enums.OrderState;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.mossy.boundedContext.coupon.domain.QCoupon.coupon;
import static com.mossy.boundedContext.coupon.domain.QUserCoupon.userCoupon;
import static com.mossy.boundedContext.marketUser.domain.QMarketSeller.marketSeller;
import static com.mossy.boundedContext.marketUser.domain.QMarketUser.marketUser;
import static com.mossy.boundedContext.order.domain.QOrder.order;
import static com.mossy.boundedContext.order.domain.QOrderItem.orderItem;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderListResponse> findOrderListByUserId(Long userId, OrderState state, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        BooleanExpression condition = order.buyer.id.eq(userId)
                .and(order.state.notIn(OrderState.PENDING, OrderState.EXPIRED));

        if (state != null) {
            condition = condition.and(order.state.eq(state));
        }

        if (startDate != null) {
            condition = condition.and(order.createdAt.goe(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            condition = condition.and(order.createdAt.lt(endDate.plusDays(1).atStartOfDay()));
        }

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

        BooleanExpression finalCondition = condition;
        return createPage(content, pageable, () -> queryFactory
                .select(order.count())
                .from(order)
                .where(finalCondition)
                .fetchOne());
    }

    @Override
    public List<OrderDetailResponse> findOrderDetailsByOrderId(Long orderId) {
        return queryFactory
                .select(Projections.constructor(OrderDetailResponse.class,
                        orderItem.id,
                        orderItem.productItemId,
                        orderItem.quantity,
                        orderItem.originalPrice,
                        orderItem.discountAmount,
                        orderItem.finalPrice,
                        coupon.couponName,
                        coupon.couponType,
                        marketSeller.storeName
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                    .join(marketSeller).on(orderItem.sellerId.eq(marketSeller.id))
                    .leftJoin(orderItem.userCoupon, userCoupon)
                    .leftJoin(userCoupon.coupon, coupon)
                .where(order.id.eq(orderId))
                .fetch();
    }

    @Override
    public Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, OrderState state, Pageable pageable) {
        BooleanExpression condition = orderItem.sellerId.eq(sellerId);

        if (state != null) {
            condition = condition.and(orderItem.state.eq(state));
        }

        List<OrderListSellerResponse> content = queryFactory
                .select(Projections.constructor(OrderListSellerResponse.class,
                        orderItem.id,
                        order.orderNo,
                        orderItem.productItemId,
                        orderItem.quantity,
                        orderItem.finalPrice,
                        orderItem.state,
                        orderItem.createdAt,
                        marketUser.name,
                        order.address
                ))
                .from(order)
                    .join(order.orderItems, orderItem)
                    .join(order.buyer, marketUser)
                .where(condition)
                .orderBy(orderItem.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        BooleanExpression finalCondition = condition;
        return createPage(content, pageable, () -> queryFactory
                .select(orderItem.count())
                .from(orderItem)
                .where(finalCondition)
                .fetchOne());
    }

    @Override
    public Page<Order> findPaidOrdersUpdatedBefore(LocalDateTime threshold, Pageable pageable) {
        BooleanExpression condition = order.state.eq(OrderState.PAID)
                .and(order.updatedAt.lt(threshold));

        List<Order> content = queryFactory
                .selectFrom(order)
                .join(order.buyer, marketUser).fetchJoin()
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.userCoupon, userCoupon).fetchJoin()
                .leftJoin(userCoupon.coupon, coupon).fetchJoin()
                .where(condition)
                .orderBy(order.updatedAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return createPage(content, pageable, () -> queryFactory
                .select(order.count())
                .from(order)
                .where(condition)
                .fetchOne());
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
