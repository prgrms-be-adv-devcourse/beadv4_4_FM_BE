package backend.mossy.boundedContext.payout.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayoutItem is a Querydsl query type for PayoutItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayoutItem extends EntityPathBase<PayoutItem> {

    private static final long serialVersionUID = 498827686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayoutItem payoutItem = new QPayoutItem("payoutItem");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<PayoutEventType> eventType = createEnum("eventType", PayoutEventType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final QPayout payout;

    public final DateTimePath<java.time.LocalDateTime> payoutDate = createDateTime("payoutDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> relId = createNumber("relId", Long.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    public final NumberPath<Long> sellerId = createNumber("sellerId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayoutItem(String variable) {
        this(PayoutItem.class, forVariable(variable), INITS);
    }

    public QPayoutItem(Path<? extends PayoutItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayoutItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayoutItem(PathMetadata metadata, PathInits inits) {
        this(PayoutItem.class, metadata, inits);
    }

    public QPayoutItem(Class<? extends PayoutItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payout = inits.isInitialized("payout") ? new QPayout(forProperty("payout"), inits.get("payout")) : null;
    }

}

