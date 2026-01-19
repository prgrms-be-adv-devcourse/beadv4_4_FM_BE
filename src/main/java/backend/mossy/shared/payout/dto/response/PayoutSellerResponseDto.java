package backend.mossy.shared.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PayoutSellerResponseDto(
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
    public static PayoutSellerResponseDto from(PayoutSeller seller) {
        return PayoutSellerResponseDto.builder()
                .id(seller.getId())
                .createdAt(seller.getCreatedAt())
                .updatedAt(seller.getUpdatedAt())
                .userId(seller.getUserId())
                .sellerType(seller.getSellerType())
                .storeName(seller.getStoreName())
                .businessNum(seller.getBusinessNum())
                .representativeName(seller.getRepresentativeName())
                .contactEmail(seller.getContactEmail())
                .contactPhone(seller.getContactPhone())
                .address1(seller.getAddress1())
                .address2(seller.getAddress2())
                .status(seller.getStatus())
                .build();
    }
}
