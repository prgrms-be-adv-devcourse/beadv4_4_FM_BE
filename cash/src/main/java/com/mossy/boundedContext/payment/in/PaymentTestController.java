package com.mossy.boundedContext.payment.in;

import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentRefundEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/test/payment")
@RequiredArgsConstructor
public class PaymentTestController {

    private final KafkaEventPublisher kafkaEventPublisher;

    @PostMapping("/refund")
    public String publishRefundEvent(
            @RequestBody RefundTestRequest request
    ) {
        PaymentRefundEvent event = new PaymentRefundEvent(
                request.orderId(),
                request.buyerId(),
                request.amount(),
                request.payMethod()
        );

        kafkaEventPublisher.publish(event);

        return "이벤트 발행 완료";
    }

    public record RefundTestRequest(
            Long orderId,
            Long buyerId,
            BigDecimal amount,
            PayMethod payMethod
    ) {}
}
