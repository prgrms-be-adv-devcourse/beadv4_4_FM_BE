package com.mossy.boundedContext.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -2116731108L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final StringPath address = createString("address");

    public final com.mossy.boundedContext.domain.market.QMarketUser buyer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final ListPath<OrderDetail, QOrderDetail> orderDetails = this.<OrderDetail, QOrderDetail>createList("orderDetails", OrderDetail.class, QOrderDetail.class, PathInits.DIRECT2);

    public final StringPath orderNo = createString("orderNo");

    public final ListPath<com.mossy.boundedContext.domain.payment.Payment, com.mossy.boundedContext.domain.payment.QPayment> payments = this.<com.mossy.boundedContext.domain.payment.Payment, com.mossy.boundedContext.domain.payment.QPayment>createList("payments", com.mossy.boundedContext.domain.payment.Payment.class, com.mossy.boundedContext.domain.payment.QPayment.class, PathInits.DIRECT2);

    public final EnumPath<com.mossy.shared.market.enums.OrderState> state = createEnum("state", com.mossy.shared.market.enums.OrderState.class);

    public final NumberPath<java.math.BigDecimal> totalPrice = createNumber("totalPrice", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new com.mossy.boundedContext.domain.market.QMarketUser(forProperty("buyer")) : null;
    }

}

