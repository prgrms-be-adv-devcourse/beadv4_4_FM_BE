package backend.mossy.shared.payout.dto.event;

import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 판매자 정보 동기화를 요청하는 DTO
 */
@Builder
public record SellerDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        SellerType sellerType,
        String storeName,
        String businessNum,
        String representativeName,
        String contactEmail,
        String contactPhone,
        String address1,
        String address2,
        SellerStatus status
) {
}
