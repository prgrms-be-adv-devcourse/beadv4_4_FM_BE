package com.mossy.kafka.publisher;

import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.member.event.UserJoinedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(Object event) {
        String topic = resolveTopicName(event);

        if (topic == null) {
            return;
        }

        kafkaTemplate.send(topic, event);
    }

    private String resolveTopicName(Object event) {
        return switch (event) {
            case UserJoinedEvent e -> KafkaTopics.USER_JOINED;
            default -> null;
        };
    }
}
