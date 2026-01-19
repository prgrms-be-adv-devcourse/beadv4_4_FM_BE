package backend.mossy.boundedContext.market.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderDetail is a Querydsl query type for OrderDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderDetail extends EntityPathBase<OrderDetail> {

    private static final long serialVersionUID = -1633386308L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderDetail orderDetail = new QOrderDetail("orderDetail");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final QOrder order;

    public final NumberPath<java.math.BigDecimal> orderPrice = createNumber("orderPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final QMarketSeller seller;

    public final EnumPath<OrderState> state = createEnum("state", OrderState.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QWeightGrade weightGrade;

    public QOrderDetail(String variable) {
        this(OrderDetail.class, forVariable(variable), INITS);
    }

    public QOrderDetail(Path<? extends OrderDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderDetail(PathMetadata metadata, PathInits inits) {
        this(OrderDetail.class, metadata, inits);
    }

    public QOrderDetail(Class<? extends OrderDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
        this.seller = inits.isInitialized("seller") ? new QMarketSeller(forProperty("seller")) : null;
        this.weightGrade = inits.isInitialized("weightGrade") ? new QWeightGrade(forProperty("weightGrade")) : null;
    }

}

