package backend.mossy.shared.member.domain.role;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = -1267987616L;

    public static final QRole role = new QRole("role");

    public final backend.mossy.global.jpa.entity.QBaseIdAndTime _super = new backend.mossy.global.jpa.entity.QBaseIdAndTime(this);

    public final EnumPath<RoleCode> code = createEnum("code", RoleCode.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

