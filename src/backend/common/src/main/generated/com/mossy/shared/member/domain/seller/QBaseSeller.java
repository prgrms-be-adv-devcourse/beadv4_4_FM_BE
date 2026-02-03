package com.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseSeller is a Querydsl query type for BaseSeller
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseSeller extends EntityPathBase<BaseSeller> {

    private static final long serialVersionUID = -37262300L;

    public static final QBaseSeller baseSeller = new QBaseSeller("baseSeller");

    public final com.mossy.global.jpa.entity.QBaseEntity _super = new com.mossy.global.jpa.entity.QBaseEntity(this);

    public final StringPath businessNum = createString("businessNum");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final EnumPath<SellerType> sellerType = createEnum("sellerType", SellerType.class);

    public final EnumPath<SellerStatus> status = createEnum("status", SellerStatus.class);

    public final StringPath storeName = createString("storeName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QBaseSeller(String variable) {
        super(BaseSeller.class, forVariable(variable));
    }

    public QBaseSeller(Path<? extends BaseSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseSeller(PathMetadata metadata) {
        super(BaseSeller.class, metadata);
    }

}

