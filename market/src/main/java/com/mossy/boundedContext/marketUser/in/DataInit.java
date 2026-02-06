package com.mossy.boundedContext.marketUser.in;

import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInit {
    private final DataInitService dataInitService;

    @Bean
    public ApplicationRunner memberDataInitApplicationRunner() {
        return args -> dataInitService.init();
    }
}

@Service
@RequiredArgsConstructor
class DataInitService {
    private final EventPublisher eventPublisher;

    @Transactional
    public void init() {
        UserPayload payload = UserPayload.builder()
                .id(2L)
                .email("test@example.com")
                .name("홍길동")
                .address("서울시 강남구")
                .nickname("모시모시")
                .profileImage("https://example.com/default.png")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build();

        eventPublisher.publish(new UserJoinedEvent(payload));


        payload = UserPayload.builder()
                .id(2L)
                .email("test@example.com")
                .name("홍길동")
                .address("서울시 서초구")
                .nickname("모시모시")
                .profileImage("https://example.com/default.png")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build();

        eventPublisher.publish(new UserUpdatedEvent(payload));
    }
}
