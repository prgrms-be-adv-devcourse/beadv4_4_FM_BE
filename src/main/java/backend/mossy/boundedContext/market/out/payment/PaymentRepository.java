package backend.mossy.boundedContext.market.out.payment;

import backend.mossy.boundedContext.market.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
