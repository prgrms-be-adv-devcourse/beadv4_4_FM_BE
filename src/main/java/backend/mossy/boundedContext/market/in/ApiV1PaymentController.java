package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.payment.PaymentFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.response.PaymentResponse;
import backend.mossy.shared.market.dto.toss.PaymentCancelCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import backend.mossy.shared.market.dto.toss.PaymentConfirmTossRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "결제 승인 및 취소 관련 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class ApiV1PaymentController {

    private final PaymentFacade paymentFacade;

    @Operation(
        summary = "주문 결제 승인 (토스)",
        description = "토스 결제창에서 인증 완료 후 전달받은 paymentKey, orderId, amount로 최종 승인을 요청합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "결제 금액 불일치 또는 잘못된 요청")
        }
    )
    @PostMapping("/confirm/toss")
    public RsData<Void> confirmPayment(@RequestBody PaymentConfirmTossRequestDto request) {
        paymentFacade.confirmTossPayment(request);
        return new RsData<>("200", "결제가 완료되었습니다.");
    }

    @Operation(
        summary = "주문 결제 승인 (예치금)",
        description = "사용자의 예치금(Cash)을 차감하여 결제를 승인 처리합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "예치금 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잔액 부족 또는 주문 정보 오류")
        }
    )
    @PostMapping("/confirm/cash")
    public RsData<Void> confirmCashPayment(@RequestBody PaymentConfirmCashRequestDto request) {
        paymentFacade.confirmCashPayment(request);
        return new RsData<>("200", "예치금 결제가 완료되었습니다.");
    }

    @Operation(
        summary = "토스 결제 취소",
        description = "이미 PG 승인된 주문을 취소하고 토스페이먼츠를 통해 환불을 진행합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "404", description = "취소할 결제 내역을 찾을 수 없음")
        }
    )
    @PostMapping("/cancel/toss")
    public RsData<Void> cancelTossPayment(@Valid @RequestBody PaymentCancelTossRequestDto request) {
        paymentFacade.cancelTossPayment(request);
        return new RsData<>("200", "PG-결제가 취소되었습니다.");
    }

    @Operation(
        summary = "예치금 결제 취소",
        description = "예치금으로 결제된 주문을 취소하고 차감되었던 예치금을 복구합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "환불 완료")
        }
    )
    @PostMapping("/cancel/cash")
    public RsData<Void> cancelCashPayment(@Valid @RequestBody PaymentCancelCashRequestDto request) {
        paymentFacade.cancelCashPayment(request);
        return new RsData<>("200", "예치금 결제가 취소되었습니다.");
    }

    @Operation(
        summary = "주문별 결제 이력 전체 조회",
        description = "특정 주문 번호(orderId)와 관련된 모든 결제 시도 및 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 주문의 결제 내역 없음")
        }
    )
    @GetMapping("/order/{orderId}") //
    public RsData<List<PaymentResponse>> getPaymentsByOrder(@PathVariable("orderId") Long orderId) {
        List<PaymentResponse> responses = paymentFacade.findAllPayments(orderId);
        return new RsData<>("200", "주문 결제 이력 조회 성공", responses);
    }
}