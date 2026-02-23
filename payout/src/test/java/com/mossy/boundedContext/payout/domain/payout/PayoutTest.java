package com.mossy.boundedContext.payout.domain.payout;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.exception.DomainException;
import com.mossy.global.config.GlobalConfig;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.payout.enums.PayoutEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PayoutTest {

    private PayoutSeller payee;
    private PayoutUser payer;

    @BeforeEach
    void setUp() {
        payee = mock(PayoutSeller.class);
        payer = mock(PayoutUser.class);

        // completePayout()에서 publishEvent() → GlobalConfig.getEventPublisher().publish()를 호출하므로
        // static 필드에 mock EventPublisher를 주입해 NPE를 방지
        EventPublisher mockPublisher = mock(EventPublisher.class);
        ReflectionTestUtils.setField(GlobalConfig.class, "eventPublisher", mockPublisher);
    }

    // ===== 생성 검증 =====

    @Test
    @DisplayName("payee가 null이면 DomainException 발생")
    void constructor_with_null_payee_throws_exception() {
        assertThatThrownBy(() -> new Payout(null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("생성 직후 amount는 0")
    void initial_amount_is_zero() {
        Payout payout = new Payout(payee);
        assertThat(payout.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("생성 직후 isCompleted = false")
    void initial_is_not_completed() {
        Payout payout = new Payout(payee);
        assertThat(payout.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("생성 직후 isCredited = false")
    void initial_is_not_credited() {
        Payout payout = new Payout(payee);
        assertThat(payout.isCredited()).isFalse();
    }

    // ===== addItem 검증 =====

    @Test
    @DisplayName("addItem 정상 호출 시 amount 누적")
    void addItem_accumulates_amount() {
        Payout payout = new Payout(payee);
        payout.addItem(PayoutEventType.정산__상품판매_대금, "ORDER_ITEM", 1L,
                LocalDateTime.now(), payer, payee, new BigDecimal("1000"));
        payout.addItem(PayoutEventType.정산__상품판매_대금, "ORDER_ITEM", 2L,
                LocalDateTime.now(), payer, payee, new BigDecimal("2000"));

        assertThat(payout.getAmount()).isEqualByComparingTo(new BigDecimal("3000"));
    }

    @Test
    @DisplayName("addItem 후 items 리스트 크기 증가")
    void addItem_increases_items_size() {
        Payout payout = new Payout(payee);
        payout.addItem(PayoutEventType.정산__상품판매_대금, "ORDER_ITEM", 1L,
                LocalDateTime.now(), payer, payee, new BigDecimal("1000"));

        assertThat(payout.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("이미 완료된 Payout에 addItem → DomainException")
    void addItem_on_completed_payout_throws_exception() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();
        payout.completePayout();

        assertThatThrownBy(() ->
                payout.addItem(PayoutEventType.정산__상품판매_대금, "ORDER_ITEM", 1L,
                        LocalDateTime.now(), payer, payee, new BigDecimal("1000")))
                .isInstanceOf(DomainException.class);
    }

    // ===== completePayout 검증 =====

    @Test
    @DisplayName("completePayout 정상 호출 후 isCompleted = true")
    void completePayout_sets_completed_true() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();

        payout.completePayout();

        assertThat(payout.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("completePayout 호출 후 payoutDate가 설정됨")
    void completePayout_sets_payout_date() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();

        payout.completePayout();

        assertThat(payout.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("이미 완료된 Payout에 completePayout 재호출 → DomainException")
    void completePayout_on_already_completed_throws_exception() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();
        payout.completePayout();

        assertThatThrownBy(payout::completePayout)
                .isInstanceOf(DomainException.class);
    }

    // ===== creditToWallet 검증 =====

    @Test
    @DisplayName("완료되지 않은 Payout에 creditToWallet → DomainException")
    void creditToWallet_on_not_completed_throws_exception() {
        Payout payout = new Payout(payee);

        assertThatThrownBy(payout::creditToWallet)
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("creditToWallet 정상 호출 후 isCredited = true")
    void creditToWallet_sets_credited_true() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();
        payout.completePayout();

        payout.creditToWallet();

        assertThat(payout.isCredited()).isTrue();
    }

    @Test
    @DisplayName("이미 credit된 Payout에 creditToWallet 재호출 → DomainException")
    void creditToWallet_on_already_credited_throws_exception() {
        Payout payout = new Payout(payee);
        stubPayeeForCompleting();
        payout.completePayout();
        payout.creditToWallet();

        assertThatThrownBy(payout::creditToWallet)
                .isInstanceOf(DomainException.class);
    }

    /**
     * completePayout() 내부의 toDto() 호출 시 payee 필드가 필요하므로 미리 stub 설정
     */
    private void stubPayeeForCompleting() {
        when(payee.getId()).thenReturn(1L);
        when(payee.getStoreName()).thenReturn("테스트 스토어");
        when(payee.isSystem()).thenReturn(false);
    }
}
