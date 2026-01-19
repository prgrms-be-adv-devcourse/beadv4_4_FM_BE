package backend.mossy.shared.member.domain.seller;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSourceSeller is a Querydsl query type for SourceSeller
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSourceSeller extends EntityPathBase<SourceSeller> {

    private static final long serialVersionUID = 1733836667L;

    public static final QSourceSeller sourceSeller = new QSourceSeller("sourceSeller");

    public final QBaseSeller _super = new QBaseSeller(this);

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

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modelTypeCode = _super.modelTypeCode;

    //inherited
    public final StringPath representativeName = _super.representativeName;

    //inherited
    public final EnumPath<SellerType> sellerType = _super.sellerType;

    //inherited
    public final EnumPath<SellerStatus> status = _super.status;

    //inherited
    public final StringPath storeName = _super.storeName;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QSourceSeller(String variable) {
        super(SourceSeller.class, forVariable(variable));
    }

    public QSourceSeller(Path<? extends SourceSeller> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSourceSeller(PathMetadata metadata) {
        super(SourceSeller.class, metadata);
    }

}

