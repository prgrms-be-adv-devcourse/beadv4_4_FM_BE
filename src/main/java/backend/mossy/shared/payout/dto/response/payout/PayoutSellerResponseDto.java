package backend.mossy.shared.payout.dto.response.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;

import java.time.LocalDateTime;

/**
 * 정산 응답에 포함될 판매자의 간략한 정보를 담는 DTO
 */
public record PayoutSellerResponseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        SellerType sellerType,
        String storeName,
        String representativeName,
        String contactEmail,
        SellerStatus status
) {
    public static PayoutSellerResponseDto from(PayoutSeller seller) {
        return new PayoutSellerResponseDto(
                seller.getId(),
                seller.getCreatedAt(),
                seller.getUpdatedAt(),
                seller.getUserId(),
                seller.getSellerType(),
                seller.getStoreName(),
                seller.getRepresentativeName(),
                seller.getContactEmail(),
                seller.getStatus()
        );
    }
}