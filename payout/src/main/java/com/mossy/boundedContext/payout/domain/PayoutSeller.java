package com.mossy.boundedContext.payout.domain;

import com.mossy.shared.member.domain.enums.SellerStatus;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.payload.SellerPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [Domain Entity] 정산 컨텍스트 내에서 사용되는 판매자(Seller) 엔티티의 복제본
 * Member 컨텍스트의 판매자 정보를 복제하여
 * Payout 컨텍스트에서 필요한 판매자 데이터를 독립적으로 관리하고 사용합니다.
 * {@link ReplicaSeller}를 상속받아 기본적인 판매자 속성을 가집니다.
 */
@Entity
@Table(name = "PAYOUT_SELLER") // Payout 컨텍스트 내에서 판매자 정보를 저장하는 테이블
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutSeller extends ReplicaSeller {

    /**
     * PayoutSeller 엔티티를 생성하는 빌더 패턴 생성자
     * {@link ReplicaSeller}의 생성자를 호출하여 판매자 기본 정보를 초기화
     */
    @Builder
    public PayoutSeller(
            Long id,
            Long userId,
            SellerType sellerType,
            String storeName,
            String businessNum,
            BigDecimal latitude,
            BigDecimal longitude,
            SellerStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(
                id,
                userId,
                sellerType,
                storeName,
                businessNum,
                latitude,
                longitude,
                status,
                createdAt,
                updatedAt
        );
    }

    /**
     * 이 PayoutSeller가 시스템 자체를 나타내는 판매자인지 여부를 확인
     * 예를 들어, 플랫폼 수수료나 기부금 수취처와 같은 시스템 계정을 구분하는 데 사용
     * @return storeName이 "system"이면 true, 그렇지 않으면 false
     */
    public boolean isSystem() {
        return "system".equals(getStoreName());
    }

    /**
     * Member 컨텍스트로부터 받은 판매자 정보로 현재 엔티티를 동기화
     * ReplicaSeller의 changeSeller를 public으로 오버라이딩
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param seller Member 컨텍스트에서 전달된 판매자 정보 DTO
     */
    @Override
    public void changeSeller(SellerPayload seller) {
        super.changeSeller(seller);
    }

    /**
     * 현재 PayoutSeller 엔티티의 핵심 정보를 담은 DTO로 변환하여 반환
     * 주로 이벤트 발행 시 이벤트 데이터로 활용되거나 다른 서비스에 정보를 전달할 때 사용
     * @return PayoutSeller의 정보를 담은 SellerDto
     */
    public SellerPayload toDto() {
        return SellerPayload.builder()
                .sellerId(getId())
                .userId(getUserId())
                .sellerType(getSellerType())
                .storeName(getStoreName())
                .businessNum(getBusinessNum())
                .status(getStatus())
                .latitude(getLatitude())
                .longitude(getLongitude())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
