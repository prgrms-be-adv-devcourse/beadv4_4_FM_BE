package com.mossy.boundedContext.marketUser.in;

import com.mossy.boundedContext.marketUser.app.MarketFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketUserKafkaEventListener {

    private final MarketFacade marketFacade;

    @KafkaListener(topics = KafkaTopics.USER_JOINED)
    public void handleUserJoinedEvent(UserJoinedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @KafkaListener(topics = KafkaTopics.USER_UPDATED)
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @KafkaListener(topics = KafkaTopics.SELLER_JOINED)
    public void handleSellerJoinedEvent(SellerJoinedEvent event) {
        marketFacade.syncSeller(event.seller());
    }

    @KafkaListener(topics = KafkaTopics.SELLER_UPDATED)
    public void handleSellerUpdatedEvent(SellerUpdatedEvent event) {
        marketFacade.syncSeller(event.seller());
    }
}
