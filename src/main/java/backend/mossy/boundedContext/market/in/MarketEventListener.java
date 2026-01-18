package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class MarketEventListener {
    private final MarketFacade  marketFacade;

//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void handle(결제 이벤트) { marketFacade.decreaseProductStock(productId, 1);}
}
