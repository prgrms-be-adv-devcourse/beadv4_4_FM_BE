package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
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
class PayoutItemTest {

    @Mock
    private Payout mockPayout;
    @Mock
    private PayoutUser mockPayer;
    @Mock
    private PayoutSeller mockPayee;

    @Test
    @DisplayName("성공: 빌더를 통해 모든 필드가 유효한 PayoutItem을 생성한다")
    void create_Success() {
        // Given
        BigDecimal amount = new BigDecimal("10000");
        LocalDateTime now = LocalDateTime.now();

        // When
        PayoutItem item = PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(now)
                .payer(mockPayer)
                .payee(mockPayee)
                .amount(amount)
                .build();

        // Then
        assertThat(item.getPayout()).isEqualTo(mockPayout);
        assertThat(item.getAmount()).isEqualByComparingTo(amount);
        assertThat(item.getRelTypeCode()).isEqualTo("ORDER_ITEM");
        assertThat(item.getEventType()).isEqualTo(PayoutEventType.정산__상품판매_대금);
    }

    @Test
    @DisplayName("실패: 정산서(Payout) 객체가 null이면 PAYOUT_IS_NULL 예외가 발생한다")
    void create_Fail_PayoutNull() {
        assertThatThrownBy(() -> PayoutItem.builder()
                .payout(null)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(BigDecimal.TEN)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYOUT_IS_NULL);
    }

    @Test
    @DisplayName("실패: 참조 엔티티 정보(relTypeCode, relId)가 누락되면 예외가 발생한다")
    void create_Fail_RelationInfoMissing() {
        // relTypeCode 가 null인 경우
        assertThatThrownBy(() -> PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode(null)
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(BigDecimal.TEN)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REL_TYPE_CODE_IS_NULL);

        // relId 가 null인 경우
        assertThatThrownBy(() -> PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER")
                .relId(null)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(BigDecimal.TEN)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REL_ID_IS_NULL);
    }

    @Test
    @DisplayName("실패: 정산 금액이 null이거나 음수이면 INVALID_PAYOUT_AMOUNT 예외가 발생한다")
    void create_Fail_InvalidAmount() {
        // 금액이 null인 경우
        assertThatThrownBy(() -> PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(null)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAYOUT_AMOUNT);

        // 금액이 음수인 경우
        assertThatThrownBy(() -> PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(new BigDecimal("-500"))
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAYOUT_AMOUNT);
    }

    @Test
    @DisplayName("성공: 지불자(payer)는 null일 수 있다 (비회원 결제 등)")
    void create_Success_PayerNull() {
        // When
        PayoutItem item = PayoutItem.builder()
                .payout(mockPayout)
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payer(null) // payer 생략
                .payee(mockPayee)
                .amount(BigDecimal.ONE)
                .build();

        // Then
        assertThat(item.getPayer()).isNull();
    }
}