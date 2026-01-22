package backend.mossy.shared.market.out;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class MarketApiClient {
    private final RestClient restClient;

    public MarketApiClient(@Value("${custom.global.internalBackUrl}") String internalBackUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(internalBackUrl + "/api/v1/market")
                .build();
    }

    public List<OrderItemDto> getOrderItems(Long id) {
        return restClient
                .get()
                .uri("/orders/%d/items".formatted(id))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}