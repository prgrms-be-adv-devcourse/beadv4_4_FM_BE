package com.mossy.member.domain.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWeightGrade is a Querydsl query type for WeightGrade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeightGrade extends EntityPathBase<WeightGrade> {

    private static final long serialVersionUID = 762150285L;

    public static final QWeightGrade weightGrade = new QWeightGrade("weightGrade");

    public final com.mossy.global.jpa.entity.QBaseIdAndTime _super = new com.mossy.global.jpa.entity.QBaseIdAndTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<java.math.BigDecimal> maxWeight = createNumber("maxWeight", java.math.BigDecimal.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath weightGradeName = createString("weightGradeName");

    public QWeightGrade(String variable) {
        super(WeightGrade.class, forVariable(variable));
    }

    public QWeightGrade(Path<? extends WeightGrade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeightGrade(PathMetadata metadata) {
        super(WeightGrade.class, metadata);
    }

}

