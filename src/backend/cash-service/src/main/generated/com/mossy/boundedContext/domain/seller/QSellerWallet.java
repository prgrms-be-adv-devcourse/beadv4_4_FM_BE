package com.mossy.boundedContext.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellerWallet is a Querydsl query type for SellerWallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellerWallet extends EntityPathBase<SellerWallet> {

    private static final long serialVersionUID = -2043264235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellerWallet sellerWallet = new QSellerWallet("sellerWallet");

    public final com.mossy.global.jpa.entity.QBaseManualIdAndTime _super = new com.mossy.global.jpa.entity.QBaseManualIdAndTime(this);

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final QCashSeller seller;

    public final ListPath<SellerCashLog, QSellerCashLog> sellerCashLogs = this.<SellerCashLog, QSellerCashLog>createList("sellerCashLogs", SellerCashLog.class, QSellerCashLog.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSellerWallet(String variable) {
        this(SellerWallet.class, forVariable(variable), INITS);
    }

    public QSellerWallet(Path<? extends SellerWallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellerWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellerWallet(PathMetadata metadata, PathInits inits) {
        this(SellerWallet.class, metadata, inits);
    }

    public QSellerWallet(Class<? extends SellerWallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.seller = inits.isInitialized("seller") ? new QCashSeller(forProperty("seller")) : null;
    }

}

