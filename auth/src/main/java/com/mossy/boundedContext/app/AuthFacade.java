package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.LoginRequest;
import com.mossy.boundedContext.in.dto.LoginResponse;
import com.mossy.boundedContext.in.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final TokenIssuer tokenIssuer;

    //로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {

        var ctx = loginUseCase.execute(request.email(), request.password());

        //TODO: null자리에 sellerId 넣기(아직 sellerClient 안만들었음)
        TokenResponse tokens = tokenIssuer.issueTokens(ctx.userId(),ctx.role(), null);
        refreshTokenUseCase.save(ctx.userId(),tokens.refreshToken());

        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());

    }

    //토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken) {

        String userIdStr = refreshTokenUseCase.validateAndGetUserId(refreshToken);
        Long userId = Long.valueOf(userIdStr);

        //TODO: 재발급 시 권한 정보(role) 어떻게 가져올지 결정
        //방법: Redis에 저장해두거나, Member 모듈에 다시 물어보기

        String role = "USER";

        refreshTokenUseCase.delete(userIdStr, refreshToken);
        TokenResponse tokens = tokenIssuer.issueTokens(userId, role, null);

        refreshTokenUseCase.save(userId, tokens.refreshToken());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //로그아웃
    @Transactional
    public  void logout(String refreshToken){

        refreshTokenUseCase.deleteIfExists(refreshToken);
    }

    //판매자 등록
    @Transactional
    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        TokenResponse tokens = tokenIssuer.issueTokens(userId, "SELLER", sellerId);
        refreshTokenUseCase.save(userId, tokens.refreshToken());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }
}
