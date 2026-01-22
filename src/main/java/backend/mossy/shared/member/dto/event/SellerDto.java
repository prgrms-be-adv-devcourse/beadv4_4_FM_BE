package backend.mossy.shared.member.dto.event;

import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SellerDto(
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

}
