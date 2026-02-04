<<<<<<<< HEAD:src/backend/market-service/src/main/java/com/mossy/boundedContext/infra/storage/config/MarketPolicyConfig.java
package com.mossy.boundedContext.infra.storage.config;
========
package com.mossy.member.infra.config;
>>>>>>>> 174e2b6 ([Chore] : auth-service 생성 및 member-service 정리):src/backend/market-service/src/main/java/com/mossy/member/infra/config/MarketPolicyConfig.java

import com.mossy.member.domain.market.MarketPolicy;
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