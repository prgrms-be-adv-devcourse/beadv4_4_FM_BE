package com.mossy.member.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliveryDistance is a Querydsl query type for DeliveryDistance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryDistance extends EntityPathBase<DeliveryDistance> {

    private static final long serialVersionUID = 1557582715L;

    public static final QDeliveryDistance deliveryDistance = new QDeliveryDistance("deliveryDistance");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> distance = createNumber("distance", Integer.class);

    public final StringPath distanceName = createString("distanceName");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDeliveryDistance(String variable) {
        super(DeliveryDistance.class, forVariable(variable));
    }

    public QDeliveryDistance(Path<? extends DeliveryDistance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryDistance(PathMetadata metadata) {
        super(DeliveryDistance.class, metadata);
    }

}

