package com.mossy.boundedContext.app.payment;

import com.mossy.boundedContext.out.payment.PaymentRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.shared.market.dto.response.PaymentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentFindAllUseCase {

//    private final PaymentRepository paymentRepository;
//    private final PaymentSupport paymentSupport;
//
//    public List<PaymentResponse> findAllPayment(String orderNo) {
//        String realOrderNo = paymentSupport.resolveOriginalOrderNo(orderNo);
//
//        List<PaymentResponse> payments = paymentRepository.findByOrderNoContaining(realOrderNo)
//            .stream()
//            .map(PaymentResponse::from)
//            .toList();
//
//        // 결과가 하나도 없으면 예외 발생
//        if (payments.isEmpty()) {
//            throw new DomainException("404", "주문(" + realOrderNo + ") 관련 결제 정보가 없습니다.");
//        }
//
//        return payments;
//    }
}