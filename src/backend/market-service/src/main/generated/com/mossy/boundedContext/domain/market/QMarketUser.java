package com.mossy.member.domain.market;

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

    private static final long serialVersionUID = 1628824391L;

    public static final QMarketUser marketUser = new QMarketUser("marketUser");

    public final com.mossy.shared.member.domain.user.QReplicaUser _super = new com.mossy.shared.member.domain.user.QReplicaUser(this);

    //inherited
    public final StringPath address = _super.address;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath email = _super.email;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final NumberPath<java.math.BigDecimal> latitude = _super.latitude;

    //inherited
    public final NumberPath<java.math.BigDecimal> longitude = _super.longitude;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath nickname = _super.nickname;

    //inherited
    public final StringPath profileImage = _super.profileImage;

    //inherited
    public final EnumPath<com.mossy.shared.member.domain.user.UserStatus> status = _super.status;

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

