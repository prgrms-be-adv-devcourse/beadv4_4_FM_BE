package backend.mossy.boundedContext.market.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketUser is a Querydsl query type for MarketUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketUser extends EntityPathBase<MarketUser> {

    private static final long serialVersionUID = 749202186L;

    public static final QMarketUser marketUser = new QMarketUser("marketUser");

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

    public QMarketUser(String variable) {
        super(MarketUser.class, forVariable(variable));
    }

    public QMarketUser(Path<? extends MarketUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketUser(PathMetadata metadata) {
        super(MarketUser.class, metadata);
    }

}

