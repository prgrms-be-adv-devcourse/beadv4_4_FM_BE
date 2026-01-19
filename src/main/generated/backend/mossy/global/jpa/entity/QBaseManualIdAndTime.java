package backend.mossy.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseManualIdAndTime is a Querydsl query type for BaseManualIdAndTime
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseManualIdAndTime extends EntityPathBase<BaseManualIdAndTime> {

    private static final long serialVersionUID = -985765228L;

    public static final QBaseManualIdAndTime baseManualIdAndTime = new QBaseManualIdAndTime("baseManualIdAndTime");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseManualIdAndTime(String variable) {
        super(BaseManualIdAndTime.class, forVariable(variable));
    }

    public QBaseManualIdAndTime(Path<? extends BaseManualIdAndTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseManualIdAndTime(PathMetadata metadata) {
        super(BaseManualIdAndTime.class, metadata);
    }

}

