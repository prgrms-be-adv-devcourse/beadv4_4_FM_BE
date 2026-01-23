package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.shared.cash.event.PaymentCompletedEvent;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import backend.mossy.shared.market.out.MarketApiClient;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.shared.member.dto.event.UserDto;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import backend.mossy.shared.member.event.SellerUpdatedEvent;
import backend.mossy.shared.member.event.UserJoinedEvent;
import backend.mossy.shared.member.event.UserUpdatedEvent;
import backend.mossy.shared.payout.dto.event.payout.PayoutEventDto;
import backend.mossy.shared.payout.event.PayoutCompletedEvent;
import backend.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutEventListenerTest {

    @InjectMocks
    private PayoutEventListener payoutEventListener;

    @Mock
    private PayoutFacade payoutFacade;

    @Mock
    private DonationFacade donationFacade;

    @Mock
    private MarketApiClient marketApiClient;

    @Test
    @DisplayName("SellerJoinedEvent가 발생하면 PayoutFacade.syncSeller가 호출된다")
    void sellerJoinedEventTest() {
        // Given
        SellerApprovedEvent sellerDto = SellerApprovedEvent.builder()
                .id(1L)
                .userId(10L)
                .sellerType(SellerType.INDIVIDUAL)
                .storeName("모시 상점")
                .businessNum("123-45-67890")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .status(SellerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SellerJoinedEvent event = new SellerJoinedEvent(sellerDto);

        // When
        payoutEventListener.sellerJoinedEvent(event);

        // Then
        verify(payoutFacade, times(1)).syncSeller(sellerDto);
    }

    @Test
    @DisplayName("SellerUpdatedEvent가 발생하면 PayoutFacade.syncSeller가 호출된다")
    void sellerUpdatedEventTest() {
        // Given
        SellerApprovedEvent sellerDto = SellerApprovedEvent.builder()
                .id(1L)
                .userId(10L)
                .sellerType(SellerType.INDIVIDUAL)
                .storeName("업데이트된 상점")
                .businessNum("123-45-67890")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .status(SellerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SellerUpdatedEvent event = new SellerUpdatedEvent(sellerDto);

        // When
        payoutEventListener.sellerUpdatedEvent(event);

        // Then
        verify(payoutFacade, times(1)).syncSeller(sellerDto);
    }

    @Test
    @DisplayName("UserJoinedEvent가 발생하면 PayoutFacade.syncUser가 호출된다")
    void userJoinedEventTest() {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@mossy.com")
                .name("테스트유저")
                .address("서울시 강남구")
                .nickname("모시유저")
                .profileImage("profile.jpg")
                .status(UserStatus.ACTIVE)
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserJoinedEvent event = new UserJoinedEvent(userDto);

        // When
        payoutEventListener.userJoinedEvent(event);

        // Then
        verify(payoutFacade, times(1)).syncUser(userDto);
    }

    @Test
    @DisplayName("UserUpdatedEvent가 발생하면 PayoutFacade.syncUser가 호출된다")
    void userUpdatedEventTest() {
        // Given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("updated@mossy.com")
                .name("업데이트된유저")
                .address("서울시 송파구")
                .nickname("업데이트유저")
                .profileImage("updated.jpg")
                .status(UserStatus.ACTIVE)
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserUpdatedEvent event = new UserUpdatedEvent(userDto);

        // When
        payoutEventListener.userUpdatedEvent(event);

        // Then
        verify(payoutFacade, times(1)).syncUser(userDto);
    }

    @Test
    @DisplayName("PayoutSellerCreatedEvent가 발생하면 PayoutFacade.createPayout이 호출된다")
    void payoutSellerCreatedEventTest() {
        // Given
        SellerApprovedEvent sellerDto = SellerApprovedEvent.builder()
                .id(5L)
                .userId(100L)
                .sellerType(SellerType.BUSINESS)
                .storeName("신규 상점")
                .businessNum("987-65-43210")
                .latitude(BigDecimal.valueOf(37.5665))
                .longitude(BigDecimal.valueOf(126.9780))
                .status(SellerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PayoutSellerCreatedEvent event = new PayoutSellerCreatedEvent(sellerDto);

        // When
        payoutEventListener.payoutSellerCreatedEvent(event);

        // Then
        verify(payoutFacade, times(1)).createPayout(5L);
    }

    @Test
    @DisplayName("PaymentCompletedEvent가 발생하면 정산 후보 생성 및 기부 로그가 생성된다")
    void paymentCompletedEventTest() {
        // Given
        Long orderId = 1000L;
        LocalDateTime paymentDate = LocalDateTime.now();
        PaymentCompletedEvent event = new PaymentCompletedEvent(orderId, paymentDate);

        // Market API가 OrderItem 목록을 반환하도록 설정
        OrderPayoutDto orderItem1 = mock(OrderPayoutDto.class);
        OrderPayoutDto orderItem2 = mock(OrderPayoutDto.class);
        List<OrderPayoutDto> orderItems = List.of(orderItem1, orderItem2);

        when(marketApiClient.getOrderItems(orderId)).thenReturn(orderItems);

        // When
        payoutEventListener.paymentCompletedEvent(event);

        // Then
        verify(marketApiClient, times(1)).getOrderItems(orderId);

        // 각 orderItem에 대해 정산 후보 추가 및 기부 로그 생성이 호출되는지 확인
        verify(payoutFacade, times(2)).addPayoutCandidateItem(any(OrderPayoutDto.class), eq(paymentDate));
        verify(donationFacade, times(2)).createDonationLog(any(OrderPayoutDto.class));

        // 구체적으로 각 아이템이 처리되었는지 확인
        verify(payoutFacade, times(1)).addPayoutCandidateItem(orderItem1, paymentDate);
        verify(payoutFacade, times(1)).addPayoutCandidateItem(orderItem2, paymentDate);
        verify(donationFacade, times(1)).createDonationLog(orderItem1);
        verify(donationFacade, times(1)).createDonationLog(orderItem2);
    }

    @Test
    @DisplayName("PayoutCompletedEvent가 발생하면 기부금 정산 및 다음 Payout이 생성된다")
    void payoutCompletedEventTest() {
        // Given
        PayoutEventDto payoutDto = PayoutEventDto.builder()
                .id(100L)
                .payeeId(50L)
                .payeeNickname("정산완료상점")
                .payoutDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(100000))
                .isSystem(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PayoutCompletedEvent event = new PayoutCompletedEvent(payoutDto);

        // When
        payoutEventListener.payoutCompletedEvent(event);

        // Then
        verify(donationFacade, times(1)).settleDonationLogs(100L);
        verify(payoutFacade, times(1)).createPayout(50L);
    }

    @Test
    @DisplayName("PaymentCompletedEvent - OrderItem이 없으면 아무것도 처리되지 않는다")
    void paymentCompletedEvent_emptyOrderItems() {
        // Given
        Long orderId = 2000L;
        LocalDateTime paymentDate = LocalDateTime.now();
        PaymentCompletedEvent event = new PaymentCompletedEvent(orderId, paymentDate);

        when(marketApiClient.getOrderItems(orderId)).thenReturn(List.of());

        // When
        payoutEventListener.paymentCompletedEvent(event);

        // Then
        verify(marketApiClient, times(1)).getOrderItems(orderId);
        verify(payoutFacade, never()).addPayoutCandidateItem(any(), any());
        verify(donationFacade, never()).createDonationLog(any());
    }
}
