package backend.mossy.boundedContext.payout.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayoutCandidateItem is a Querydsl query type for PayoutCandidateItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayoutCandidateItem extends EntityPathBase<PayoutCandidateItem> {

    private static final long serialVersionUID = 1163966979L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayoutCandidateItem payoutCandidateItem = new QPayoutCandidateItem("payoutCandidateItem");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<PayoutEventType> eventType = createEnum("eventType", PayoutEventType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final QPayoutUser payee;

    public final DateTimePath<java.time.LocalDateTime> payoutDate = createDateTime("payoutDate", java.time.LocalDateTime.class);

    public final QPayoutItem payoutItem;

    public final NumberPath<Long> relId = createNumber("relId", Long.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayoutCandidateItem(String variable) {
        this(PayoutCandidateItem.class, forVariable(variable), INITS);
    }

    public QPayoutCandidateItem(Path<? extends PayoutCandidateItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayoutCandidateItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayoutCandidateItem(PathMetadata metadata, PathInits inits) {
        this(PayoutCandidateItem.class, metadata, inits);
    }

    public QPayoutCandidateItem(Class<? extends PayoutCandidateItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payee = inits.isInitialized("payee") ? new QPayoutUser(forProperty("payee")) : null;
        this.payoutItem = inits.isInitialized("payoutItem") ? new QPayoutItem(forProperty("payoutItem"), inits.get("payoutItem")) : null;
    }

}

