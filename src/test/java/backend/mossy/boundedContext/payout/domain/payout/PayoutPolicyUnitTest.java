package backend.mossy.boundedContext.payout.domain.payout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutPolicyUnitTest {

    @Test
    @DisplayName("성공: setPayoutReadyWaitingDays 호출 시 static 필드 값이 변경된다")
    void setPayoutReadyWaitingDays_UpdatesStaticField() {
        // Given
        PayoutPolicy policy = new PayoutPolicy();
        int newDays = 14;

        // When
        policy.setPayoutReadyWaitingDays(newDays);

        // Then
        assertThat(PayoutPolicy.PAYOUT_READY_WAITING_DAYS).isEqualTo(newDays);
    }
}