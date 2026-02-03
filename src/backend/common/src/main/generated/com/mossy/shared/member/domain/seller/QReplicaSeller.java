package com.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReplicaSeller is a Querydsl query type for ReplicaSeller
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QReplicaSeller extends EntityPathBase<ReplicaSeller> {

    private static final long serialVersionUID = -35859613L;

    public static final QReplicaSeller replicaSeller = new QReplicaSeller("replicaSeller");

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

    public QReplicaSeller(String variable) {
        super(ReplicaSeller.class, forVariable(variable));
    }

    public QReplicaSeller(Path<? extends ReplicaSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReplicaSeller(PathMetadata metadata) {
        super(ReplicaSeller.class, metadata);
    }

}

