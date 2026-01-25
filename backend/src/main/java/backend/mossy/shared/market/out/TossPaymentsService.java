package backend.mossy.shared.market.out;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.toss.TossCancelRequest;
import backend.mossy.shared.market.dto.toss.TossCancelResponse;
import backend.mossy.shared.market.dto.toss.TossConfirmRequest;
import backend.mossy.shared.market.dto.toss.TossConfirmResponse;
import backend.mossy.shared.market.dto.toss.TossPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
public class TossPaymentsService {

    private final RestClient tossRestClient;
    private final String encodedSecretKey;

    public TossPaymentsService(
        @Value("${toss.payments.base-url}") String tossBaseUrl,
        @Value("${toss.payments.secret-key}") String tossSecretKey
    ) {
        this.tossRestClient = RestClient.builder()
            .baseUrl(tossBaseUrl)
            .build();
        this.encodedSecretKey = Base64.getEncoder()
            .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    //토스페이먼츠 결제 승인 API 호출
    public TossConfirmResponse confirm(TossConfirmRequest request) {
        log.info("토스 결제 승인 요청: paymentKey={}, orderId={}, amount={}",
            request.paymentKey(), request.orderId(), request.amount());

        try {
            TossConfirmResponse response = tossRestClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.error("토스페이먼츠 결제 승인 실패: status={}, body={}", res.getStatusCode(), errorBody);
                    throw new DomainException(ErrorCode.TOSS_PAYMENT_CONFIRM_FAILED);
                })
                .body(TossConfirmResponse.class);

            log.info("토스 결제 승인 성공: paymentKey={}", response.paymentKey());
            return response;

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            log.error("토스페이먼츠 결제 승인 예외: {}", e.getMessage(), e);
            throw new DomainException(ErrorCode.TOSS_PAYMENT_CONFIRM_FAILED);
        }
    }
    //토스페이먼츠 결제 취소 API 호출
    public TossCancelResponse cancel(String paymentKey, String cancelReason) {
        try {
            TossCancelResponse response = tossRestClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TossCancelRequest(cancelReason))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("토스페이먼츠 결제 취소 실패: status={}", res.getStatusCode());
                    throw new DomainException(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED);
                })
                .body(TossCancelResponse.class);
            return response;

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED);
        }
    }

    // orderId로 토스 결제 상세 조회
    public TossPaymentResponse getPaymentByOrderId(String orderId) {
        log.info("토스 결제 조회 요청: orderId={}", orderId);

        try {
            return tossRestClient.get()
                // 스크린샷의 API 경로: /v1/payments/orders/{orderId}
                .uri("/v1/payments/orders/" + orderId)
                .header("Authorization", "Basic " + encodedSecretKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("토스 결제 조회 실패: status={}", res.getStatusCode());
                    throw new DomainException(ErrorCode.TOSS_PAYMENT_NOT_FOUND);
                })
                .body(TossPaymentResponse.class);

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            log.error("토스 결제 조회 중 예외 발생: {}", e.getMessage());
            throw new DomainException(ErrorCode.TOSS_API_ERROR);
        }
    }
}

