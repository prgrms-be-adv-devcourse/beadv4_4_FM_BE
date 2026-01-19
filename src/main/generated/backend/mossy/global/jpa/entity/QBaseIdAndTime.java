package backend.mossy.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseIdAndTime is a Querydsl query type for BaseIdAndTime
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseIdAndTime extends EntityPathBase<BaseIdAndTime> {

    private static final long serialVersionUID = 377189498L;

    public static final QBaseIdAndTime baseIdAndTime = new QBaseIdAndTime("baseIdAndTime");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseIdAndTime(String variable) {
        super(BaseIdAndTime.class, forVariable(variable));
    }

    public QBaseIdAndTime(Path<? extends BaseIdAndTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseIdAndTime(PathMetadata metadata) {
        super(BaseIdAndTime.class, metadata);
    }

}

