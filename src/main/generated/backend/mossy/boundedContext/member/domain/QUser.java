package backend.mossy.boundedContext.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 163438668L;

    public static final QUser user = new QUser("user");

    public final backend.mossy.shared.member.domain.user.QSourceUser _super = new backend.mossy.shared.member.domain.user.QSourceUser(this);

    //inherited
    public final StringPath address = _super.address;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath email = _super.email;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath nickname = _super.nickname;

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    //inherited
    public final StringPath profileImage = _super.profileImage;

    public final StringPath rrnEncrypted = createString("rrnEncrypted");

    //inherited
    public final EnumPath<backend.mossy.shared.member.domain.user.UserStatus> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<backend.mossy.shared.member.domain.role.UserRole, backend.mossy.shared.member.domain.role.QUserRole> userRoles = this.<backend.mossy.shared.member.domain.role.UserRole, backend.mossy.shared.member.domain.role.QUserRole>createList("userRoles", backend.mossy.shared.member.domain.role.UserRole.class, backend.mossy.shared.member.domain.role.QUserRole.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

