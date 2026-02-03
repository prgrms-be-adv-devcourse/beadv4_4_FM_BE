package com.mossy.boundedContext.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCashSeller is a Querydsl query type for CashSeller
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCashSeller extends EntityPathBase<CashSeller> {

    private static final long serialVersionUID = -1585553777L;

    public static final QCashSeller cashSeller = new QCashSeller("cashSeller");

    public final com.mossy.shared.member.domain.seller.QReplicaSeller _super = new com.mossy.shared.member.domain.seller.QReplicaSeller(this);

    //inherited
    public final StringPath businessNum = _super.businessNum;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final NumberPath<java.math.BigDecimal> latitude = _super.latitude;

    //inherited
    public final NumberPath<java.math.BigDecimal> longitude = _super.longitude;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final EnumPath<com.mossy.shared.member.domain.seller.SellerType> sellerType = _super.sellerType;

    //inherited
    public final EnumPath<com.mossy.shared.member.domain.seller.SellerStatus> status = _super.status;

    //inherited
    public final StringPath storeName = _super.storeName;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> userId = _super.userId;

    public QCashSeller(String variable) {
        super(CashSeller.class, forVariable(variable));
    }

    public QCashSeller(Path<? extends CashSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCashSeller(PathMetadata metadata) {
        super(CashSeller.class, metadata);
    }

}

