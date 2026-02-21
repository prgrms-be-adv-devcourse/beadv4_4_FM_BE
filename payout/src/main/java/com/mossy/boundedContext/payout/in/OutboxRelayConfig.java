package com.mossy.boundedContext.payout.in;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OutboxRelayConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean("stringKafkaTemplate")
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");              // 모든 복제본 저장 확인 후 성공 처리
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 재전송 시 중복 메시지 방지
        config.put(ProducerConfig.RETRIES_CONFIG, 5);               // Kafka 내부 재시도 횟수 제한
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config));
    }
}
