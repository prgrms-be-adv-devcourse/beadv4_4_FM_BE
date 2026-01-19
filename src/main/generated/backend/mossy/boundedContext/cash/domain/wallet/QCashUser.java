package backend.mossy.boundedContext.cash.domain.wallet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCashUser is a Querydsl query type for CashUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCashUser extends EntityPathBase<CashUser> {

    private static final long serialVersionUID = -398974567L;

    public static final QCashUser cashUser = new QCashUser("cashUser");

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

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QCashUser(String variable) {
        super(CashUser.class, forVariable(variable));
    }

    public QCashUser(Path<? extends CashUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCashUser(PathMetadata metadata) {
        super(CashUser.class, metadata);
    }

}

