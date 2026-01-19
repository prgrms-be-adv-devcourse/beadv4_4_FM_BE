package backend.mossy.boundedContext.cash.out;

import backend.mossy.boundedContext.cash.domain.core.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
