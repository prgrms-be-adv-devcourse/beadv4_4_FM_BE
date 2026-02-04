package com.mossy.boundedContext.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserWallet is a Querydsl query type for UserWallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserWallet extends EntityPathBase<UserWallet> {

    private static final long serialVersionUID = 1640671573L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserWallet userWallet = new QUserWallet("userWallet");

    public final com.mossy.global.jpa.entity.QBaseManualIdAndTime _super = new com.mossy.global.jpa.entity.QBaseManualIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QCashUser user;

    public final ListPath<UserCashLog, QUserCashLog> userCashLogs = this.<UserCashLog, QUserCashLog>createList("userCashLogs", UserCashLog.class, QUserCashLog.class, PathInits.DIRECT2);

    public QUserWallet(String variable) {
        this(UserWallet.class, forVariable(variable), INITS);
    }

    public QUserWallet(Path<? extends UserWallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserWallet(PathMetadata metadata, PathInits inits) {
        this(UserWallet.class, metadata, inits);
    }

    public QUserWallet(Class<? extends UserWallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QCashUser(forProperty("user")) : null;
    }

}

