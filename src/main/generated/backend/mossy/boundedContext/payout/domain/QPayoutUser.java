package backend.mossy.boundedContext.payout.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayoutUser is a Querydsl query type for PayoutUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayoutUser extends EntityPathBase<PayoutUser> {

    private static final long serialVersionUID = 499184222L;

    public static final QPayoutUser payoutUser = new QPayoutUser("payoutUser");

    public final backend.mossy.shared.member.domain.user.QReplicaUser _super = new backend.mossy.shared.member.domain.user.QReplicaUser(this);

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

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final StringPath phoneNum = _super.phoneNum;

    //inherited
    public final StringPath profileImage = _super.profileImage;

    //inherited
    public final StringPath rrnEncrypted = _super.rrnEncrypted;

    //inherited
    public final EnumPath<backend.mossy.shared.member.domain.user.UserStatus> status = _super.status;

    public final BooleanPath system = createBoolean("system");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayoutUser(String variable) {
        super(PayoutUser.class, forVariable(variable));
    }

    public QPayoutUser(Path<? extends PayoutUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayoutUser(PathMetadata metadata) {
        super(PayoutUser.class, metadata);
    }

}

