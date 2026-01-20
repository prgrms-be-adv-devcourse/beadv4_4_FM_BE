package backend.mossy.boundedContext.market.app.order;

import backend.mossy.shared.market.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final CreateOrderUseCase createOrderUseCase;

    @Transactional
    public void createOrder(PaymentCompletedEvent event) {
        createOrderUseCase.createOrder(event);
    }
}
