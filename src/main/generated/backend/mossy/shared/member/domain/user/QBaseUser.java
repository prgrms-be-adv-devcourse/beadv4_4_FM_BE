package backend.mossy.shared.member.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseUser is a Querydsl query type for BaseUser
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseUser extends EntityPathBase<BaseUser> {

    private static final long serialVersionUID = 670113905L;

    public static final QBaseUser baseUser = new QBaseUser("baseUser");

    public final backend.mossy.global.jpa.entity.QBaseEntity _super = new backend.mossy.global.jpa.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath rrnEncrypted = createString("rrnEncrypted");

    public final EnumPath<UserStatus> status = createEnum("status", UserStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBaseUser(String variable) {
        super(BaseUser.class, forVariable(variable));
    }

    public QBaseUser(Path<? extends BaseUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseUser(PathMetadata metadata) {
        super(BaseUser.class, metadata);
    }

}

