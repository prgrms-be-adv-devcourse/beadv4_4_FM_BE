package com.mossy.boundedContext.payment.out;


import com.mossy.boundedContext.payment.domain.Payment;
import java.util.List;
import java.util.Optional;

import com.mossy.shared.cash.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderNoAndStatus(String orderNo, PaymentStatus status);

    List<Payment> findByOrderNoContaining(String orderNo);
}
