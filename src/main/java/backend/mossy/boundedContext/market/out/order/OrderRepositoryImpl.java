package backend.mossy.boundedContext.market.out.order;

import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import backend.mossy.shared.market.dto.response.OrderDetailResponse;
import backend.mossy.shared.market.dto.response.OrderListResponse;
import backend.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import backend.mossy.shared.market.dto.response.OrderListSellerResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Supplier;

import static backend.mossy.boundedContext.market.domain.market.QMarketSeller.marketSeller;
import static backend.mossy.boundedContext.market.domain.market.QMarketUser.marketUser;
import static backend.mossy.boundedContext.market.domain.order.QDeliveryDistance.deliveryDistance;
import static backend.mossy.boundedContext.market.domain.order.QOrder.order;
import static backend.mossy.boundedContext.market.domain.order.QOrderDetail.orderDetail;
import static backend.mossy.boundedContext.market.domain.order.QWeightGrade.weightGrade;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderPayoutDto> findPayoutOrderByOrderId(Long orderId) {
        return queryFactory
                .select(Projections.constructor(OrderPayoutDto.class,
                        orderDetail.id,
                        order.id,
                        marketUser.id,
                        marketUser.name,
                        orderDetail.seller.id,
                        orderDetail.productId,
                        orderDetail.orderPrice,
                        weightGrade.weightGradeName,
                        deliveryDistance.distance.castToNum(java.math.BigDecimal.class),
                        orderDetail.createdAt,
                        orderDetail.updatedAt
                ))
                .from(order)
                    .join(order.orderDetails, orderDetail)
                    .join(order.buyer, marketUser)
                    .join(orderDetail.weightGrade, weightGrade)
                    .join(orderDetail.deliveryDistance, deliveryDistance)
                .where(order.id.eq(orderId))
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
                        orderDetail.count(),
                        order.address,
                        order.createdAt
                ))
                .from(order)
                    .join(order.orderDetails, orderDetail)
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
                        orderDetail.productId,
                        orderDetail.quantity,
                        orderDetail.orderPrice,
                        marketSeller.storeName
                ))
                .from(order)
                    .join(order.orderDetails, orderDetail)
                    .join(orderDetail.seller, marketSeller)
                .where(order.id.eq(orderId))
                .fetch();
    }

    @Override
    public Page<OrderListSellerResponse> findSellerOrderListBySellerId(Long sellerId, Pageable pageable) {
        BooleanExpression condition = orderDetail.seller.id.eq(sellerId);

        List<OrderListSellerResponse> content = queryFactory
                .select(Projections.constructor(OrderListSellerResponse.class,
                        orderDetail.id,
                        orderDetail.productId,
                        orderDetail.quantity,
                        orderDetail.orderPrice,
                        order.state,
                        orderDetail.createdAt
                ))
                .from(order)
                    .join(order.orderDetails, orderDetail)
                .where(condition)
                .orderBy(orderDetail.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return createPage(content, pageable, () -> queryFactory
                .select(orderDetail.count())
                .from(orderDetail)
                .where(condition)
                .fetchOne());
    }

    @Override
    public OrderDetailSellerResponse findSellerOrderDetailById(Long orderDetailId) {
        return queryFactory
                .select(Projections.constructor(OrderDetailSellerResponse.class,
                        order.orderNo,
                        marketUser.name,
                        order.address,
                        weightGrade.weightGradeName,
                        deliveryDistance.distance
                ))
                .from(order)
                    .join(order.orderDetails, orderDetail)
                    .join(order.buyer, marketUser)
                    .join(orderDetail.weightGrade, weightGrade)
                    .join(orderDetail.deliveryDistance, deliveryDistance)
                .where(orderDetail.id.eq(orderDetailId))
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