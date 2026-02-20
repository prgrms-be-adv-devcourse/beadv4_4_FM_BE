package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.user.User;
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

    // --- [Entity → Payload (이벤트 발행용)] ---

    UserPayload toPayload(User user);

    // --- [조회 응답 매핑] ---

    default UserInfoDto toUserInfoDto(User user, SellerRequestStatus status) {
        return new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getName(),
                status
        );
    }

    default MemberAuthInfoResponse toMemberAuthInfoResponse(User user, List<RoleCode> roles, Long sellerId) {
        return new MemberAuthInfoResponse(
                user.getId(),
                roles,
                sellerId,
                true
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
