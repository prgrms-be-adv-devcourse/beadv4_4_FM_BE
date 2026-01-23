package backend.mossy.boundedContext.payout.domain.payout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // 테스트용 프로파일 사용 시
class PayoutPolicyTest {

    @Autowired
    private PayoutPolicy payoutPolicy;

    @Test
    @DisplayName("성공: 외부 설정값이 없으면 기본값인 7일로 초기화되어야 한다")
    void policy_DefaultValue_Success() {
        // static 필드이므로 클래스 명으로 직접 접근하여 확인
        assertThat(PayoutPolicy.PAYOUT_READY_WAITING_DAYS).isEqualTo(7);
    }
}