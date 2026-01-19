package backend.mossy.shared.member.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSourceUser is a Querydsl query type for SourceUser
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QSourceUser extends EntityPathBase<SourceUser> {

    private static final long serialVersionUID = -615246981L;

    public static final QSourceUser sourceUser = new QSourceUser("sourceUser");

    public final QBaseUser _super = new QBaseUser(this);

    //inherited
    public final StringPath address = _super.address;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    //inherited
    public final StringPath email = _super.email;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath nickname = _super.nickname;

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final StringPath phoneNum = _super.phoneNum;

    //inherited
    public final StringPath profileImage = _super.profileImage;

    //inherited
    public final StringPath rrnEncrypted = _super.rrnEncrypted;

    //inherited
    public final EnumPath<UserStatus> status = _super.status;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QSourceUser(String variable) {
        super(SourceUser.class, forVariable(variable));
    }

    public QSourceUser(Path<? extends SourceUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSourceUser(PathMetadata metadata) {
        super(SourceUser.class, metadata);
    }

}

