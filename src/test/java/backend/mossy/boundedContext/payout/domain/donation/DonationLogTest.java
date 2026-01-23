package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DonationLogTest {

    @Test
    @DisplayName("성공: 올바른 정보가 입력되면 DonationLog가 생성된다")
    void createDonationLog_Success() {
        // given
        PayoutUser user = PayoutUser.builder().id(1L).build();
        BigDecimal amount = new BigDecimal("1000");
        Double carbonG = 125.5;

        // when
        DonationLog log = DonationLog.builder()
                .user(user)
                .orderItemId(100L)
                .amount(amount)
                .carbonOffsetG(carbonG)
                .build();

        // then
        assertThat(log.getUser()).isEqualTo(user);
        assertThat(log.getAmount()).isEqualTo(amount);
        assertThat(log.getIsSettled()).isFalse(); // 기본값 검증
    }

    @Test
    @DisplayName("실패: 기부 금액이 0원 이하이면 예외가 발생한다 (검증 로직 테스트)")
    void createDonationLog_Fail_InvalidAmount() {
        // given
        PayoutUser user = PayoutUser.builder().id(1L).build();
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // when & then
        assertThatThrownBy(() -> DonationLog.builder()
                .user(user)
                .orderItemId(100L)
                .amount(zeroAmount)
                .carbonOffsetG(10.0)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DONATION_AMOUNT);
    }

    @Test
    @DisplayName("성공: settle()을 호출하면 정산 상태가 true로 변경된다")
    void settle_Success() {
        // given
        DonationLog log = DonationLog.builder()
                .user(PayoutUser.builder().id(1L).build())
                .orderItemId(100L)
                .amount(new BigDecimal("1000"))
                .carbonOffsetG(10.0)
                .build();

        // when
        log.settle();

        // then
        assertThat(log.getIsSettled()).isTrue();
    }

    @Test
    @DisplayName("실패: 이미 정산된 로그를 다시 settle() 하면 예외가 발생한다")
    void settle_Fail_AlreadySettled() {
        // given
        DonationLog log = DonationLog.builder()
                .user(PayoutUser.builder().id(1L).build())
                .orderItemId(100L)
                .amount(new BigDecimal("1000"))
                .carbonOffsetG(10.0)
                .build();
        log.settle(); // 1차 정산 완료

        // when & then
        assertThatThrownBy(log::settle)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_SETTLED_DONATION);
    }
}