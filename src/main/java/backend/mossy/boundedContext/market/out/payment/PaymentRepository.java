package backend.mossy.boundedContext.market.out.payment;

import backend.mossy.boundedContext.market.domain.payment.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<List<Payment>> findAllByOrderId(Long orderId);
}
