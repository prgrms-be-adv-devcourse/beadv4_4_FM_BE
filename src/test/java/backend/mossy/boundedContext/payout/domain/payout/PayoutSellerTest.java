package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutSellerTest {

    @Test
    @DisplayName("성공: 빌더를 통해 PayoutSeller가 정상적으로 생성되어야 한다")
    void create_Success() {
        // Given
        Long sellerId = 1L;
        String storeName = "모시상점";

        // When
        PayoutSeller seller = PayoutSeller.builder()
                .id(sellerId)
                .storeName(storeName)
                .sellerType(SellerType.BUSINESS)
                .status(SellerStatus.ACTIVE)
                .build();

        // Then
        assertThat(seller.getId()).isEqualTo(sellerId);
        assertThat(seller.getStoreName()).isEqualTo(storeName);
        assertThat(seller.getSellerType()).isEqualTo(SellerType.BUSINESS);
    }

    @Test
    @DisplayName("성공: storeName이 'system'이면 isSystem은 true를 반환한다")
    void isSystem_True_Success() {
        // When
        PayoutSeller systemSeller = PayoutSeller.builder()
                .storeName("system")
                .build();

        // Then
        assertThat(systemSeller.isSystem()).isTrue();
    }

    @Test
    @DisplayName("성공: storeName이 'system'이 아니면 isSystem은 false를 반환한다")
    void isSystem_False_Success() {
        // When
        PayoutSeller normalSeller = PayoutSeller.builder()
                .storeName("일반상점")
                .build();

        // Then
        assertThat(normalSeller.isSystem()).isFalse();
    }

    @Test
    @DisplayName("성공: toDto 호출 시 엔티티 정보가 SellerApprovedEvent DTO로 올바르게 변환된다")
    void toDto_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PayoutSeller seller = PayoutSeller.builder()
                .id(10L)
                .userId(100L)
                .storeName("테스트상점")
                .sellerType(SellerType.BUSINESS)
                .status(SellerStatus.ACTIVE)
                .businessNum("123-45-67890")
                .latitude(new BigDecimal("37.123"))
                .longitude(new BigDecimal("127.123"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        SellerApprovedEvent dto = seller.toDto();

        // Then
        assertThat(dto.id()).isEqualTo(seller.getId());
        assertThat(dto.storeName()).isEqualTo(seller.getStoreName());
        assertThat(dto.latitude()).isEqualByComparingTo(seller.getLatitude());
        assertThat(dto.createdAt()).isEqualTo(now);
    }
}