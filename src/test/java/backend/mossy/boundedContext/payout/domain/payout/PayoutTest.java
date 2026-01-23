package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PayoutTest {

    @Mock
    private PayoutSeller mockPayee;
    @Mock
    private PayoutUser mockPayer;

    private Payout payout;

    /**
     * 핵심: Payout을 상속받아 문제가 되는 메서드를 '무력화'시킨 클래스입니다.
     * 이 클래스는 PayoutTest 내부에 있으므로 외부 패키지 제약에서 자유롭습니다.
     */
    private static class PayoutForTest extends Payout {
        public PayoutForTest(PayoutSeller payee) {
            super(payee);
        }

        @Override
        protected void publishEvent(Object event) {
            // 부모(BaseEntity)의 publishEvent를 호출하지 않음 (super 호출 X)
            // 이를 통해 GlobalConfig 관련 NPE를 원천 차단합니다.
        }
    }

    @BeforeEach
    void setUp() {
        payout = new PayoutForTest(mockPayee);
    }

    @Test
    @DisplayName("이미 완료된 정산에는 항목을 추가할 수 없다")
    void addItem_Fail_AlreadyCompleted() {
        payout.completePayout(); // 이제 NPE 없이 실행됩니다.

        assertThatThrownBy(() -> payout.addItem(
                PayoutEventType.정산__상품판매_대금, "ORDER", 1L, LocalDateTime.now(), mockPayer, mockPayee, BigDecimal.TEN
        ))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_COMPLETED_PAYOUT);
    }

    @Test
    @DisplayName("이미 완료된 정산을 다시 완료 처리할 수 없다")
    void completePayout_Fail_AlreadyCompleted() {
        payout.completePayout();

        assertThatThrownBy(() -> payout.completePayout())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_COMPLETED_PAYOUT);
    }

    @Test
    @DisplayName("addItem 성공: 정산 항목이 추가되고 총 금액이 합산된다")
    void addItem_Success() {
        BigDecimal amount = new BigDecimal("1000");

        payout.addItem(PayoutEventType.정산__상품판매_대금, "ORDER", 1L, LocalDateTime.now(), mockPayer, mockPayee, amount);

        assertThat(payout.getAmount()).isEqualByComparingTo(amount);
        assertThat(payout.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("수취인 없이 Payout을 생성하면 PAYOUT_SELLER_NOT_FOUND 예외가 발생한다")
    void constructor_Fail_NullPayee() {
        // Given & When & Then을 한 번에 검증
        assertThatThrownBy(() -> {
            new Payout(null);
        })
                .isInstanceOf(DomainException.class)
                .extracting("errorCode") // 필드를 직접 추출해서 비교하는 방식이 더 명확합니다.
                .isEqualTo(ErrorCode.PAYOUT_SELLER_NOT_FOUND);
    }
}