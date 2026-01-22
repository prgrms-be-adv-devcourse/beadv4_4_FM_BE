package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.payment.PaymentFacade;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.toss.PaymentCancelCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmTossRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class ApiV1PaymentController {

    private final PaymentFacade paymentFacade;

    @Operation(summary = "주문 결제 승인 (토스)", description = "토스 결제창에서 결제 완료 후 승인 처리")
    @PostMapping("/confirm/toss")
    public RsData<Void> confirmPayment(@RequestBody PaymentConfirmTossRequestDto request) {
        paymentFacade.confirmTossPayment(request);
        return new RsData<>("P-200", "결제가 완료되었습니다.");
    }

    @Operation(summary = "주문 결제 승인 (예치금)", description = "예치금을 이용한 결제 승인 처리")
    @PostMapping("/confirm/cash")
    public RsData<Void> confirmCashPayment(@RequestBody PaymentConfirmCashRequestDto request) {
        paymentFacade.confirmCashPayment(request);
        return new RsData<>("P-200", "예치금 결제가 완료되었습니다.");
    }

    @Operation(summary = "결제 취소", description = "PG 결제된 주문을 취소하고 환불 처리")
    @PostMapping("/cancel/toss")
    public RsData<Void> cancelTossPayment(@Valid @RequestBody PaymentCancelTossRequestDto request) {
        paymentFacade.cancelTossPayment(request);
        return new RsData<>("P-200", "결제가 취소되었습니다.");
    }

    @Operation(summary = "결제 취소", description = "예치금으로 결제된 주문을 취소하고 환불 처리")
    @PostMapping("/cancel/cash")
    public RsData<Void> cancelCashPayment(@Valid @RequestBody PaymentCancelCashRequestDto request) {
        paymentFacade.cancelCashPayment(request);
        return new RsData<>("P-200", "결제가 취소되었습니다.");
    }
}
