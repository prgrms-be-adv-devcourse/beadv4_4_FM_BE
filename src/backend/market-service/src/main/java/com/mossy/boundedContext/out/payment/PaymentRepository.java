package com.mossy.boundedContext.out.payment;

import com.mossy.boundedContext.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

import com.mossy.shared.market.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<List<Payment>> findAllByOrderNo(String orderNo);

    Optional<Payment> findByOrderNoAndStatus(String orderNo, PaymentStatus status);

    List<Payment> findByOrderNoContaining(String orderNo);
}
