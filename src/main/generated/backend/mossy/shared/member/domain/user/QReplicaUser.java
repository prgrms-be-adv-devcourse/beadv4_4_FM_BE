package backend.mossy.shared.member.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReplicaUser is a Querydsl query type for ReplicaUser
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QReplicaUser extends EntityPathBase<ReplicaUser> {

    private static final long serialVersionUID = 1432707662L;

    public static final QReplicaUser replicaUser = new QReplicaUser("replicaUser");

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

    public QReplicaUser(String variable) {
        super(ReplicaUser.class, forVariable(variable));
    }

    public QReplicaUser(Path<? extends ReplicaUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReplicaUser(PathMetadata metadata) {
        super(ReplicaUser.class, metadata);
    }

}

