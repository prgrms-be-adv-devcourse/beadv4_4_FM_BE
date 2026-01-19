package backend.mossy.boundedContext.cash.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentReference is a Querydsl query type for PaymentReference
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPaymentReference extends BeanPath<PaymentReference> {

    private static final long serialVersionUID = -1532379097L;

    public static final QPaymentReference paymentReference = new QPaymentReference("paymentReference");

    public final NumberPath<Long> buyerId = createNumber("buyerId", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final StringPath pgUid = createString("pgUid");

    public QPaymentReference(String variable) {
        super(PaymentReference.class, forVariable(variable));
    }

    public QPaymentReference(Path<? extends PaymentReference> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentReference(PathMetadata metadata) {
        super(PaymentReference.class, metadata);
    }

}

