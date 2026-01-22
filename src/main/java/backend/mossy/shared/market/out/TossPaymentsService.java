package backend.mossy.shared.market.out;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.toss.TossCancelRequest;
import backend.mossy.shared.market.dto.toss.TossCancelResponse;
import backend.mossy.shared.market.dto.toss.TossConfirmRequest;
import backend.mossy.shared.market.dto.toss.TossConfirmResponse;
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
        try {
            TossConfirmResponse response = tossRestClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("토스페이먼츠 결제 승인 실패: status={}", res.getStatusCode());
                    throw new DomainException(ErrorCode.TOSS_PAYMENT_CONFIRM_FAILED);
                })
                .body(TossConfirmResponse.class);


            return response;

        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
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
}

