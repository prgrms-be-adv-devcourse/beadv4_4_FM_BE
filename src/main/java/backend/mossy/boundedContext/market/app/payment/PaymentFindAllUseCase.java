package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.out.payment.PaymentRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.market.dto.response.PaymentResponse; // 위에서 만든 DTO import
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentFindAllUseCase {

    private final PaymentRepository paymentRepository;

    public List<PaymentResponse> findAllPayment(String orderNo) {
        return paymentRepository.findAllByOrderNo(orderNo)
            .orElseThrow(() -> new DomainException("404", "주문 관련 결제 정보가 없습니다."))
            .stream()
            .map(PaymentResponse::from)
            .toList();
    }
}