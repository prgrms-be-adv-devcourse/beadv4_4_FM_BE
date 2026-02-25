package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.domain.user.UserSocialAccount;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.out.external.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.payload.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserPayload toPayload(User user);

    default UserInfoDto toUserInfoDto(User user, SellerRequestStatus status) {
        List<String> providers = user.getSocialAccounts() != null
                ? user.getSocialAccounts().stream()
                    .map(UserSocialAccount::getProvider)
                    .toList()
                : List.of();

        boolean hasPassword = !user.isSocialOnly();

        // 프로필 이미지 정책 적용
        String profileImage = user.getProfileImage();
        if (profileImage != null && !profileImage.isBlank()) {
            if (profileImage.contains("default-seller")) {
                profileImage = "default-seller";
            } else if (profileImage.contains("default-user") || profileImage.contains("default-seller")) {
                profileImage = "default-user";
            }
        }

        return new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getName(),
                profileImage,
                user.getRrnEncrypted(),
                user.getPhoneNum(),
                user.getAddress(),
                status,
                providers,
                hasPassword
        );
    }

    default MemberAuthInfoResponse toMemberAuthInfoResponse(User user, List<RoleCode> roles, Long sellerId) {
        return new MemberAuthInfoResponse(
                user.getId(),
                roles,
                sellerId,
                !user.isPending()  // PENDING이면 active=false
        );
    }

    default MemberVerifyExternResponse toMemberVerifyResponse(User user, List<RoleCode> roles, boolean isValid) {
        return new MemberVerifyExternResponse(
                user.getId(),
                roles,
                isValid
        );
    }

    default SocialLonginResponse toSocialLoginResponse(User user, List<String> roleNames, boolean isNewUser) {
        return new SocialLonginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                roleNames,
                isNewUser
        );
    }
}
