package backend.mossy.boundedContext.market.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketSeller is a Querydsl query type for MarketSeller
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketSeller extends EntityPathBase<MarketSeller> {

    private static final long serialVersionUID = -1641186562L;

    public static final QMarketSeller marketSeller = new QMarketSeller("marketSeller");

    public final backend.mossy.shared.member.domain.seller.QReplicaSeller _super = new backend.mossy.shared.member.domain.seller.QReplicaSeller(this);

    //inherited
    public final StringPath address1 = _super.address1;

    //inherited
    public final StringPath address2 = _super.address2;

    //inherited
    public final StringPath businessNum = _super.businessNum;

    //inherited
    public final StringPath contactEmail = _super.contactEmail;

    //inherited
    public final StringPath contactPhone = _super.contactPhone;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final StringPath representativeName = _super.representativeName;

    //inherited
    public final EnumPath<backend.mossy.shared.member.domain.seller.SellerType> sellerType = _super.sellerType;

    //inherited
    public final EnumPath<backend.mossy.shared.member.domain.seller.SellerStatus> status = _super.status;

    //inherited
    public final StringPath storeName = _super.storeName;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> userId = _super.userId;

    public QMarketSeller(String variable) {
        super(MarketSeller.class, forVariable(variable));
    }

    public QMarketSeller(Path<? extends MarketSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketSeller(PathMetadata metadata) {
        super(MarketSeller.class, metadata);
    }

}

