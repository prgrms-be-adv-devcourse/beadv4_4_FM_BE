package com.mossy.member.domain.market;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketSeller is a Querydsl query type for MarketSeller
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketSeller extends EntityPathBase<MarketSeller> {

    private static final long serialVersionUID = 1862162427L;

    public static final QMarketSeller marketSeller = new QMarketSeller("marketSeller");

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

    public QMarketSeller(String variable) {
        super(MarketSeller.class, forVariable(variable));
    }

    public QMarketSeller(Path<? extends MarketSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketSeller(PathMetadata metadata) {
        super(MarketSeller.class, metadata);
    }

}

