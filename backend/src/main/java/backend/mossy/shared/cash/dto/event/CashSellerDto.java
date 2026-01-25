package backend.mossy.shared.cash.dto.event;

import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashSellerDto(
    Long id,
    Long userId,
    SellerType sellerType,
    String storeName,
    String businessNum,
    String representativeName,
    String contactEmail,
    String contactPhone,
    String address1,
    String address2,
    BigDecimal latitude,
    BigDecimal longitude,
    SellerStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CashSellerDto from(CashSeller seller) {
        return CashSellerDto.builder()
            .id(seller.getId())
            .userId(seller.getUserId())
            .sellerType(seller.getSellerType())
            .storeName(seller.getStoreName())
            .businessNum(seller.getBusinessNum())
            .latitude(seller.getLatitude())
            .longitude(seller.getLongitude())
            .createdAt(seller.getCreatedAt())
            .updatedAt(seller.getUpdatedAt())
            .build();
    }
}