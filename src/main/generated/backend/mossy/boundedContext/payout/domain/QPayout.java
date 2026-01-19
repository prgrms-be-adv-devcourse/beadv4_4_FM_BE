package backend.mossy.boundedContext.payout.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayout is a Querydsl query type for Payout
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayout extends EntityPathBase<Payout> {

    private static final long serialVersionUID = 617726963L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayout payout = new QPayout("payout");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final ListPath<PayoutItem, QPayoutItem> items = this.<PayoutItem, QPayoutItem>createList("items", PayoutItem.class, QPayoutItem.class, PathInits.DIRECT2);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final QPayoutUser payee;

    public final DateTimePath<java.time.LocalDateTime> payoutDate = createDateTime("payoutDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayout(String variable) {
        this(Payout.class, forVariable(variable), INITS);
    }

    public QPayout(Path<? extends Payout> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayout(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayout(PathMetadata metadata, PathInits inits) {
        this(Payout.class, metadata, inits);
    }

    public QPayout(Class<? extends Payout> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payee = inits.isInitialized("payee") ? new QPayoutUser(forProperty("payee")) : null;
    }

}

