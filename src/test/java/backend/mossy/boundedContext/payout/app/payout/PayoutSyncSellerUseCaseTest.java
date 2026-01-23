package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static backend.mossy.shared.member.domain.seller.SellerStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutSyncSellerUseCaseTest {

    @InjectMocks
    private PayoutSyncSellerUseCase payoutSyncSellerUseCase;

    @Mock
    private PayoutSellerRepository payoutSellerRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Test
    @DisplayName("성공: 새로운 판매자 정보가 들어오면 저장하고 Created 이벤트를 발행한다")
    void syncSeller_New_Success() {
        // given
        Long sellerId = 1L;
        SellerApprovedEvent event = createEvent(sellerId);

        given(payoutSellerRepository.existsById(sellerId)).willReturn(false);

        // Mock 객체 대신 실제 빌더로 만든 객체를 사용하거나,
        // 메서드 체이닝을 검증하기 위해 빌더로 생성된 엔티티 반환
        PayoutSeller savedSeller = PayoutSeller.builder()
                .id(sellerId)
                .storeName("테스트 상점")
                .build();

        given(payoutSellerRepository.save(any(PayoutSeller.class))).willReturn(savedSeller);

        // when
        payoutSyncSellerUseCase.syncSeller(event);

        // then
        verify(payoutSellerRepository).save(any(PayoutSeller.class));
        verify(eventPublisher, times(1)).publish(any(PayoutSellerCreatedEvent.class));
    }

    @Test
    @DisplayName("성공: 기존에 존재하던 판매자라면 업데이트만 하고 이벤트를 발행하지 않는다")
    void syncSeller_Existing_Success() {
        // given
        Long sellerId = 1L;
        SellerApprovedEvent event = createEvent(sellerId);

        given(payoutSellerRepository.existsById(sellerId)).willReturn(true);

        PayoutSeller savedSeller = PayoutSeller.builder()
                .id(sellerId)
                .build();

        given(payoutSellerRepository.save(any(PayoutSeller.class))).willReturn(savedSeller);

        // when
        payoutSyncSellerUseCase.syncSeller(event);

        // then
        verify(payoutSellerRepository).save(any(PayoutSeller.class));
        verify(eventPublisher, never()).publish(any(PayoutSellerCreatedEvent.class));
    }

    @Test
    @DisplayName("실패: 유효하지 않은 판매자 데이터(null)가 들어오면 예외가 발생한다")
    void syncSeller_Fail_InvalidData() {
        // when & then
        assertThatThrownBy(() -> payoutSyncSellerUseCase.syncSeller(null))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_SELLER_DATA);
    }

    /**
     * Helper 메서드: 빌더 패턴을 사용하여 이벤트 객체 생성
     */
    private SellerApprovedEvent createEvent(Long sellerId) {
        return SellerApprovedEvent.builder()
                .id(sellerId)
                .userId(10L)
                .storeName("테스트 상점")
                .businessNum("123-45-67890")
                .sellerType(SellerType.INDIVIDUAL)
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}