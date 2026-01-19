package backend.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseSeller is a Querydsl query type for BaseSeller
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseSeller extends EntityPathBase<BaseSeller> {

    private static final long serialVersionUID = -563181071L;

    public static final QBaseSeller baseSeller = new QBaseSeller("baseSeller");

    public final backend.mossy.global.jpa.entity.QBaseEntity _super = new backend.mossy.global.jpa.entity.QBaseEntity(this);

    public final StringPath address1 = createString("address1");

    public final StringPath address2 = createString("address2");

    public final StringPath businessNum = createString("businessNum");

    public final StringPath contactEmail = createString("contactEmail");

    public final StringPath contactPhone = createString("contactPhone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    public final StringPath representativeName = createString("representativeName");

    public final EnumPath<SellerType> sellerType = createEnum("sellerType", SellerType.class);

    public final EnumPath<SellerStatus> status = createEnum("status", SellerStatus.class);

    public final StringPath storeName = createString("storeName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QBaseSeller(String variable) {
        super(BaseSeller.class, forVariable(variable));
    }

    public QBaseSeller(Path<? extends BaseSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseSeller(PathMetadata metadata) {
        super(BaseSeller.class, metadata);
    }

}

