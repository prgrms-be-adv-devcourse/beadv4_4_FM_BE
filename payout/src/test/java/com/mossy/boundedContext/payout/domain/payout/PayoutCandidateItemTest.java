package com.mossy.boundedContext.payout.domain.payout;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.exception.DomainException;
import com.mossy.shared.cash.enums.SellerEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PayoutCandidateItemTest {

    private PayoutSeller payee;
    private PayoutUser payer;

    @BeforeEach
    void setUp() {
        payee = mock(PayoutSeller.class);
        payer = mock(PayoutUser.class);
    }

    // ===== 필수 필드 검증 =====

    @Test
    @DisplayName("eventType이 null → DomainException")
    void null_event_type_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(null)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("relTypeCode가 null → DomainException")
    void null_rel_type_code_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode(null)
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("relTypeCode가 공백 → DomainException")
    void blank_rel_type_code_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("   ")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("relId가 null → DomainException")
    void null_rel_id_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(null)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("paymentDate가 null → DomainException")
    void null_payment_date_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(null)
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("payee가 null → DomainException")
    void null_payee_throws_exception() {
        assertThatThrownBy(() -> PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(null)
                .amount(new BigDecimal("1000"))
                .build())
                .isInstanceOf(DomainException.class);
    }

    // ===== amount 기본값 검증 =====

    @Test
    @DisplayName("amount가 null이면 BigDecimal.ZERO로 기본값 설정")
    void null_amount_defaults_to_zero() {
        PayoutCandidateItem item = PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(null)
                .build();

        assertThat(item.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ===== 정상 생성 검증 =====

    @Test
    @DisplayName("모든 필드 정상 입력 시 객체 생성 및 필드 검증")
    void valid_item_creation() {
        LocalDateTime paymentDate = LocalDateTime.of(2025, 1, 1, 12, 0);

        PayoutCandidateItem item = PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(paymentDate)
                .payer(payer)
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("30"))
                .carbonKg(new BigDecimal("0.125"))
                .build();

        assertThat(item.getEventType()).isEqualTo(SellerEventType.정산__상품판매_대금);
        assertThat(item.getRelTypeCode()).isEqualTo("ORDER_ITEM");
        assertThat(item.getRelId()).isEqualTo(1L);
        assertThat(item.getPaymentDate()).isEqualTo(paymentDate);
        assertThat(item.getPayer()).isEqualTo(payer);
        assertThat(item.getPayee()).isEqualTo(payee);
        assertThat(item.getAmount()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(item.getWeightGrade()).isEqualTo("소형");
        assertThat(item.getDeliveryDistance()).isEqualByComparingTo(new BigDecimal("30"));
        assertThat(item.getCarbonKg()).isEqualByComparingTo(new BigDecimal("0.125"));
    }

    @Test
    @DisplayName("초기에 payoutItem은 null (미처리 상태)")
    void initial_payout_item_is_null() {
        PayoutCandidateItem item = PayoutCandidateItem.builder()
                .eventType(SellerEventType.정산__상품판매_대금)
                .relTypeCode("ORDER_ITEM")
                .relId(1L)
                .paymentDate(LocalDateTime.now())
                .payee(payee)
                .amount(new BigDecimal("1000"))
                .build();

        assertThat(item.getPayoutItem()).isNull();
    }
}
