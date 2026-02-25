package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.domain.enums.SellerType;
import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SellerSummaryResponse(
        Long sellerId,
        Long userId,
        String email,
        String name,
        String nickname,
        String storeName,
        SellerType sellerType,
        String contactEmail,
        String contactPhone,
        UserStatus userStatus,
        LocalDateTime createdAt
) {
    public static SellerSummaryResponse from(Seller seller, User user) {
        return SellerSummaryResponse.builder()
                .sellerId(seller.getId())
                .userId(seller.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .storeName(seller.getStoreName())
                .sellerType(seller.getSellerType())
                .contactEmail(seller.getContactEmail())
                .contactPhone(seller.getContactPhone())
                .userStatus(user.getStatus())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}
