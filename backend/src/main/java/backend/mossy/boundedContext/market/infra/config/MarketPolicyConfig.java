package backend.mossy.boundedContext.market.infra.config;

import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarketPolicyConfig {

    @Bean
    public MarketPolicy marketPolicy(
            @Value("${market.policy.cart.max_quantity}") int maxQuantity
    ) {
        return new MarketPolicy(maxQuantity);
    }
}