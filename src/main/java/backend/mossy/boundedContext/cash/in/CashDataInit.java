package backend.mossy.boundedContext.cash.in;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.shared.cash.dto.response.WalletResponseDto;
import backend.mossy.shared.member.dto.common.UserDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CashDataInit {

    private final CashFacade cashFacade;

    @Bean
    @Order(1)
    public ApplicationRunner cashDataInitApplicationRunner() {
        return args -> {
            log.info("=== Cash 초기 데이터 생성 시작 ===");

            // 회원 데이터 정의
            UserDto[] users = {
                UserDto.builder()
                    .id(1L)
                    .name("shin")
                    .email("shin@naver.com")
                    .nickname("짱구")
                    .address("서울시 행복구 떡잎마을")
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build(),
                UserDto.builder()
                    .id(2L)
                    .name("kim")
                    .email("kim@gmail.com")
                    .nickname("철수")
                    .address("서울시 행복구 떡잎마을")
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build(),
                UserDto.builder()
                    .id(3L)
                    .name("lee")
                    .email("lee@daum.net")
                    .nickname("유리")
                    .address("서울시 행복구 떡잎마을")
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build(),
                UserDto.builder()
                    .id(4L)
                    .name("park")
                    .email("park@kakao.com")
                    .nickname("맹구")
                    .address("서울시 행복구 떡잎마을")
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build(),
                UserDto.builder()
                    .id(5L)
                    .name("jung")
                    .email("jung@naver.com")
                    .nickname("훈이")
                    .address("서울시 행복구 떡잎마을")
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            };
            for (UserDto userDto : users) {
                // 멤버 싱크 및 지갑 생성
                cashFacade.syncUser(userDto);

                // 2. 로그는 싱크 완료만 찍습니다. 지갑은 이벤트 리스너가 알아서 만들 것입니다.
                log.info("회원 가입/싱크 요청 완료: {}", userDto.email());
            }
            log.info("=== 이벤트 처리 대기 및 검증 시작 ===");

            // 검증
            verifyResult();
        };
    }

    public void verifyResult() {
        log.info("=== 검증 시작 ===");

        // 이메일 대신 Long 타입 ID 배열 사용 (1L부터 5L까지)
        Long[] userIds = {1L, 2L, 3L, 4L, 5L};

        for (Long userId : userIds) {
            CashUser user = cashFacade.findCashUserById(userId);

            WalletResponseDto walletDto = cashFacade.findByWalletByUserId(userId);

            log.info("검증 OK - 회원: [{}] {}, 지갑 잔액: {}",
                user.getId(), user.getName(), walletDto.balance());
        }

        log.info("=== 전체 검증 성공 ===");
    }
}