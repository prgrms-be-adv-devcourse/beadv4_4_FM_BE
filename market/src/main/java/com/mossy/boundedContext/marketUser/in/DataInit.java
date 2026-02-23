package com.mossy.boundedContext.marketUser.in;

import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "data-init.enabled", havingValue = "true", matchIfMissing = false)
public class DataInit implements ApplicationRunner {
    private final DataInitService dataInitService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== MarketUser DataInit 시작 ===");
        dataInitService.init();
        log.info("=== MarketUser DataInit 완료 ===");
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
