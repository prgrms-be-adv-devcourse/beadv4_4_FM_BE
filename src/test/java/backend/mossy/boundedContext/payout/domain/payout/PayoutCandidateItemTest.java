package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.payout.dto.event.payout.CreatePayoutCandidateItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PayoutCandidateItemTest {

    @Mock
    private PayoutUser mockPayer;

    @Mock
    private PayoutSeller mockPayee;

    @Test
    @DisplayName("성공: 모든 필드가 유효하면 PayoutCandidateItem이 생성된다")
    void create_Success() {
        // Given
        BigDecimal amount = new BigDecimal("15000");
        LocalDateTime now = LocalDateTime.now();

        // When
        PayoutCandidateItem item = PayoutCandidateItem.builder()
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(100L)
                .paymentDate(now)
                .payer(mockPayer)
                .payee(mockPayee)
                .amount(amount)
                .build();

        // Then
        assertThat(item.getEventType()).isEqualTo(PayoutEventType.정산__상품판매_대금);
        assertThat(item.getAmount()).isEqualByComparingTo(amount);
        assertThat(item.getPayee()).isEqualTo(mockPayee);
        assertThat(item.getPayoutItem()).isNull(); // 생성 직후에는 null이어야 함
    }

    @Test
    @DisplayName("실패: 필수 값(EventType, Payee 등)이 누락되면 예외가 발생한다")
    void create_Fail_NullFields() {
        // EventType 누락 시
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYOUT_EVENT_TYPE_IS_NULL);

        // Payee(수취인) 누락 시
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYEE_IS_NULL);
    }

    @Test
    @DisplayName("실패: 금액(amount)이 음수이면 INVALID_AMOUNT 예외가 발생한다")
    void create_Fail_InvalidAmount() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(new BigDecimal("-1"))
                .build())
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_AMOUNT);
    }

    @Test
    @DisplayName("성공: amount가 null이면 기본값 0으로 설정된다")
    void create_Success_DefaultAmount() {
        // When
        PayoutCandidateItem item = PayoutCandidateItem.builder()
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(null)
                .build();

        // Then
        assertThat(item.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("성공: DTO로부터 엔티티를 생성하는 팩토리 메서드 검증")
    void from_Dto_Success() {
        // Given
        CreatePayoutCandidateItemDto dto = new CreatePayoutCandidateItemDto(
                PayoutEventType.정산__상품판매_대금,
                "ORDER_ITEM",
                1L,
                LocalDateTime.now(),
                mockPayer,
                mockPayee,
                new BigDecimal("5000")
        );

        // When
        PayoutCandidateItem item = PayoutCandidateItem.from(dto);

        // Then
        assertThat(item.getRelId()).isEqualTo(dto.relId());
        assertThat(item.getAmount()).isEqualByComparingTo(dto.amount());
    }

    @Test
    @DisplayName("성공: PayoutItem이 연결되면 상태가 변경된다")
    void setPayoutItem_Success() {
        // Given
        PayoutCandidateItem candidateItem = createDefaultItem();
        PayoutItem mockPayoutItem = mock(PayoutItem.class);

        // When
        candidateItem.setPayoutItem(mockPayoutItem);

        // Then
        assertThat(candidateItem.getPayoutItem()).isNotNull();
        assertThat(candidateItem.getPayoutItem()).isEqualTo(mockPayoutItem);
    }

    // 테스트 헬퍼 메서드
    private PayoutCandidateItem createDefaultItem() {
        return PayoutCandidateItem.builder()
                .eventType(PayoutEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(mockPayee)
                .amount(BigDecimal.TEN)
                .build();
    }
}