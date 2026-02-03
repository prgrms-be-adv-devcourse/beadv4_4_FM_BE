package com.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSellerRequest is a Querydsl query type for SellerRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellerRequest extends EntityPathBase<SellerRequest> {

    private static final long serialVersionUID = 1674825500L;

    public static final QSellerRequest sellerRequest = new QSellerRequest("sellerRequest");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final NumberPath<Long> activeUserId = createNumber("activeUserId", Long.class);

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final StringPath businessNum = createString("businessNum");

    public final StringPath contactEmail = createString("contactEmail");

    public final StringPath contactPhone = createString("contactPhone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final StringPath representativeName = createString("representativeName");

    public final EnumPath<SellerType> sellerType = createEnum("sellerType", SellerType.class);

    public final EnumPath<SellerRequestStatus> status = createEnum("status", SellerRequestStatus.class);

    public final StringPath storeName = createString("storeName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSellerRequest(String variable) {
        super(SellerRequest.class, forVariable(variable));
    }

    public QSellerRequest(Path<? extends SellerRequest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSellerRequest(PathMetadata metadata) {
        super(SellerRequest.class, metadata);
    }

}

