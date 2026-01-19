package backend.mossy.shared.payout.dto.response;

import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutSellerDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private SellerType sellerType;
    private String storeName;
    private String businessNum;
    private String representativeName;
    private String contactEmail;
    private String contactPhone;
    private String address1;
    private String address2;
    private SellerStatus status;
}
