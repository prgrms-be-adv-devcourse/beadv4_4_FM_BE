package backend.mossy.boundedContext.cash.domain.history;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCashLog is a Querydsl query type for CashLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCashLog extends EntityPathBase<CashLog> {

    private static final long serialVersionUID = -129080035L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCashLog cashLog = new QCashLog("cashLog");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<EventType> eventType = createEnum("eventType", EventType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final NumberPath<Integer> relId = createNumber("relId", Integer.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final backend.mossy.boundedContext.cash.domain.wallet.QCashUser user;

    public final backend.mossy.boundedContext.cash.domain.wallet.QWallet wallet;

    public QCashLog(String variable) {
        this(CashLog.class, forVariable(variable), INITS);
    }

    public QCashLog(Path<? extends CashLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCashLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCashLog(PathMetadata metadata, PathInits inits) {
        this(CashLog.class, metadata, inits);
    }

    public QCashLog(Class<? extends CashLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new backend.mossy.boundedContext.cash.domain.wallet.QCashUser(forProperty("user")) : null;
        this.wallet = inits.isInitialized("wallet") ? new backend.mossy.boundedContext.cash.domain.wallet.QWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

