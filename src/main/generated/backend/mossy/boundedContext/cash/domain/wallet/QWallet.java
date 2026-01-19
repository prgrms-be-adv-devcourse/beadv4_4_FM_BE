package backend.mossy.boundedContext.cash.domain.wallet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWallet is a Querydsl query type for Wallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWallet extends EntityPathBase<Wallet> {

    private static final long serialVersionUID = 2095984308L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWallet wallet = new QWallet("wallet");

    public final backend.mossy.global.jpa.entity.QBaseManualIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseManualIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    public final ListPath<backend.mossy.boundedContext.cash.domain.history.CashLog, backend.mossy.boundedContext.cash.domain.history.QCashLog> cashLogs = this.<backend.mossy.boundedContext.cash.domain.history.CashLog, backend.mossy.boundedContext.cash.domain.history.QCashLog>createList("cashLogs", backend.mossy.boundedContext.cash.domain.history.CashLog.class, backend.mossy.boundedContext.cash.domain.history.QCashLog.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QCashUser user;

    public QWallet(String variable) {
        this(Wallet.class, forVariable(variable), INITS);
    }

    public QWallet(Path<? extends Wallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWallet(PathMetadata metadata, PathInits inits) {
        this(Wallet.class, metadata, inits);
    }

    public QWallet(Class<? extends Wallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QCashUser(forProperty("user")) : null;
    }

}

