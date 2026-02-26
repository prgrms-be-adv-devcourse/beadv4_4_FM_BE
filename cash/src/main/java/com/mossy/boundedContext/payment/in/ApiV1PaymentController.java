package com.mossy.boundedContext.payment.in;

import com.mossy.boundedContext.payment.app.PaymentFacade;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmCashRequestDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmTossRequestDto;
import com.mossy.boundedContext.payment.in.dto.response.PaymentResponse;
import com.mossy.boundedContext.payment.in.dto.response.TossPaymentResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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
    public RsData<Void> confirmTossPayment(@RequestBody PaymentConfirmTossRequestDto request) {
        paymentFacade.confirmTossPayment(request);
        return RsData.success(SuccessCode.TOSS_PAYMENT_CONFIRMED);
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
        return RsData.success(SuccessCode.CASH_PAYMENT_CONFIRMED);
    }

    @Operation(
        summary = "결제 취소 (전체/부분 환불)",
        description = "주문 번호를 기반으로 결제 수단을 자동 파악하여 결제를 취소합니다. ids가 없으면 전체 환불, ids가 있으면 부분 환불합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "404", description = "취소할 결제 내역을 찾을 수 없음")
        }
    )
    @PostMapping("/cancel")
    public RsData<Void> cancelPayment(@Valid @RequestBody PaymentCancelRequestDto request) {
        paymentFacade.cancelPayment(request);
        return RsData.success(SuccessCode.TOSS_PAYMENT_CANCELLED);
    }

    @Operation(
        summary = "주문별 결제 이력 전체 조회",
        description = "특정 주문 번호(orderId)와 관련된 모든 결제 시도 및 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 주문의 결제 내역 없음")
        }
    )
    @GetMapping("/orders/{orderNo}")
    public RsData<Page<PaymentResponse>> getPaymentsByOrder(
        @PathVariable String orderNo,
        @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> responses = paymentFacade.findAllPayments(orderNo, pageable);
        return RsData.success(SuccessCode.PAYMENT_HISTORY_FOUND, responses);
    }

    @Operation(
        summary = "토스 결제 원본 정보 조회",
        description = "우리 시스템의 주문 번호(orderNo)를 이용해 토스페이먼츠 서버에 기록된 원본 결제 상세 정보를 직접 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "토스 결제 정보 조회 성공"),
            @ApiResponse(responseCode = "502", description = "토스 API 통신 실패 또는 내역 없음")
        }
    )
    @GetMapping("/toss/orders/{orderNo}")
    public RsData<TossPaymentResponse> getTossPaymentInfo(
        @Parameter(description = "주문 고유 번호", example = "ORD_20240123_abc123")
        @PathVariable String orderNo
    ) {
        TossPaymentResponse response = paymentFacade.findTossPayment(orderNo);
        return RsData.success(SuccessCode.TOSS_PAYMENT_INFO_FOUND, response);
    }
}