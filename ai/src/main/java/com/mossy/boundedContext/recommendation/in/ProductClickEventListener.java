package com.mossy.boundedContext.recommendation.in;

import com.mossy.boundedContext.recommendation.out.repository.UserProductClickRepository;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.product.event.ProductClickedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClickEventListener {

    private final UserProductClickRepository userProductClickRepository;

    @KafkaListener(topics = KafkaTopics.PRODUCT_CLICKED, groupId = "ai")
    public void handleProductClicked(ProductClickedEvent event) {
        log.info("상품 클릭 이벤트 수신: userId={}, productId={}", event.userId(), event.productId());

        userProductClickRepository.upsertClick(event.userId(), event.productId())
            .subscribe(
                result -> log.info("클릭 이력 저장 완료: userId={}, productId={}, clickCount={}",
                    event.userId(), event.productId(), result.getClickCount()),
                error -> log.error("클릭 이력 저장 실패: userId={}, productId={}",
                    event.userId(), event.productId(), error)
            );
    }
}

