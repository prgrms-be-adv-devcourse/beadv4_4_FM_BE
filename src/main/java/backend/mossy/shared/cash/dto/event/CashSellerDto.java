package backend.mossy.shared.cash.dto.event;

import backend.mossy.boundedContext.cash.domain.seller.CashSeller;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashSellerDto(
    Long id,                    // 판매자 고유 식별자 (seller_id)
    Long userId,                // 연결된 유저 ID
    SellerType sellerType,      // 판매자 유형 (INDIVIDUAL, BUSINESS)
    String storeName,           // 상호명
    String businessNum,         // 사업자 번호
    String representativeName,  // 대표자 이름
    String contactEmail,        // 담당자 이메일
    String contactPhone,        // 담당자 전화번호
    String address1,            // 주소 1
    String address2,            // 주소 2
    SellerStatus status,        // 판매자 상태
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CashSellerDto from(CashSeller seller) {
        return CashSellerDto.builder()
            .id(seller.getId())
            .userId(seller.getUserId()) // BaseSeller 필드
            .sellerType(seller.getSellerType())
            .storeName(seller.getStoreName())
            .businessNum(seller.getBusinessNum())
            .representativeName(seller.getRepresentativeName())
            .contactEmail(seller.getContactEmail())
            .contactPhone(seller.getContactPhone())
            .address1(seller.getAddress1())
            .address2(seller.getAddress2())
            .createdAt(seller.getCreatedAt())
            .updatedAt(seller.getUpdatedAt())
            .build();
    }
}