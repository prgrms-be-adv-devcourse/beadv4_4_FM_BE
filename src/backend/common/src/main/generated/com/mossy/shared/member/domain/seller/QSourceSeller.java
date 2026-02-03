package com.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSourceSeller is a Querydsl query type for SourceSeller
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QSourceSeller extends EntityPathBase<SourceSeller> {

    private static final long serialVersionUID = 335634670L;

    public static final QSourceSeller sourceSeller = new QSourceSeller("sourceSeller");

    public final QBaseSeller _super = new QBaseSeller(this);

    //inherited
    public final StringPath businessNum = _super.businessNum;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final NumberPath<java.math.BigDecimal> latitude = _super.latitude;

    //inherited
    public final NumberPath<java.math.BigDecimal> longitude = _super.longitude;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final EnumPath<SellerType> sellerType = _super.sellerType;

    //inherited
    public final EnumPath<SellerStatus> status = _super.status;

    //inherited
    public final StringPath storeName = _super.storeName;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> userId = _super.userId;

    public QSourceSeller(String variable) {
        super(SourceSeller.class, forVariable(variable));
    }

    public QSourceSeller(Path<? extends SourceSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSourceSeller(PathMetadata metadata) {
        super(SourceSeller.class, metadata);
    }

}

