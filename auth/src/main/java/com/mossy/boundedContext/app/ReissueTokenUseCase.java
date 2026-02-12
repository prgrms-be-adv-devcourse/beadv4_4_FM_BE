package com.mossy.boundedContext.app;

import com.mossy.boundedContext.global.jwt.JwtProvider;
import com.mossy.boundedContext.in.dto.response.TokenResponse;
import com.mossy.boundedContext.out.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.external.MemberFeignClient;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.role.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {

    private final JwtProvider jwtProvider;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final IssueTokenUseCase issueTokenUseCase;
    private final MemberFeignClient memberFeignClient;

    @Transactional
    public TokenResponse executes(String oldRefreshToken) {
        //1. old RT에서 userId 추출(서명/만료 검증 포함)
        final Long userId;
        try {
            userId = jwtProvider.getUserId(oldRefreshToken);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }

        //2. Member에서 최신 권한/상태 조회
        MemberAuthInfoResponse authInfo;
        try {
            authInfo = memberFeignClient.getAuthInfo(userId);
        } catch (feign.FeignException e) {
            if (e.status() == 404) throw new DomainException(ErrorCode.USER_NOT_FOUND);
            throw new DomainException(ErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            throw new DomainException(ErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        if (!authInfo.active()) {
            throw new DomainException(ErrorCode.ACCOUNT_DISABLED);
        }

        //3. roles -> 토큰에 넣을 대표 role 선택
        String role = pickPrimaryRole(authInfo.roles());

        //4. 새 토큰 발급
        TokenResponse newTokens = issueTokenUseCase.execute(
                userId,
                role,
                authInfo.sellerId()
        );

        //5. RTR 원자 교체
        refreshTokenUseCase.rotate(oldRefreshToken, newTokens.refreshToken());

        return newTokens;
    }

    private String pickPrimaryRole(List<RoleCode> roles) {
        if (roles == null || roles.isEmpty()) return "USER";

        if (roles.contains(RoleCode.ADMIN)) return "ADMIN";
        if (roles.contains(RoleCode.SELLER)) return "SELLER";
        return "USER";
    }
}
