package com.mossy.boundedContext.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellerCashLog is a Querydsl query type for SellerCashLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellerCashLog extends EntityPathBase<SellerCashLog> {

    private static final long serialVersionUID = 519435125L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellerCashLog sellerCashLog = new QSellerCashLog("sellerCashLog");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<com.mossy.shared.cash.enums.SellerEventType> eventType = createEnum("eventType", com.mossy.shared.cash.enums.SellerEventType.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final NumberPath<Long> relId = createNumber("relId", Long.class);

    public final StringPath relTypeCode = createString("relTypeCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QCashSeller user;

    public final QSellerWallet wallet;

    public QSellerCashLog(String variable) {
        this(SellerCashLog.class, forVariable(variable), INITS);
    }

    public QSellerCashLog(Path<? extends SellerCashLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellerCashLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellerCashLog(PathMetadata metadata, PathInits inits) {
        this(SellerCashLog.class, metadata, inits);
    }

    public QSellerCashLog(Class<? extends SellerCashLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QCashSeller(forProperty("user")) : null;
        this.wallet = inits.isInitialized("wallet") ? new QSellerWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

