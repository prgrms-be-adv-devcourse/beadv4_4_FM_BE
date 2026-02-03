package com.mossy.boundedContext.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCashLog is a Querydsl query type for UserCashLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCashLog extends EntityPathBase<UserCashLog> {

    private static final long serialVersionUID = -1242671819L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCashLog userCashLog = new QUserCashLog("userCashLog");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<com.mossy.shared.cash.enums.UserEventType> eventType = createEnum("eventType", com.mossy.shared.cash.enums.UserEventType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final NumberPath<Long> relId = createNumber("relId", Long.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QCashUser user;

    public final QUserWallet wallet;

    public QUserCashLog(String variable) {
        this(UserCashLog.class, forVariable(variable), INITS);
    }

    public QUserCashLog(Path<? extends UserCashLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCashLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCashLog(PathMetadata metadata, PathInits inits) {
        this(UserCashLog.class, metadata, inits);
    }

    public QUserCashLog(Class<? extends UserCashLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QCashUser(forProperty("user")) : null;
        this.wallet = inits.isInitialized("wallet") ? new QUserWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

