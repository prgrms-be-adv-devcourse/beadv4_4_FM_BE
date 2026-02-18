package com.mossy.boundedContext.app;

import  com.mossy.boundedContext.in.dto.request.LoginRequest;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.in.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final IssueTokenUseCase issueTokenUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;

    //로그인
    public LoginResponse login(LoginRequest request) {

        var ctx = loginUseCase.execute(request.email(), request.password());

        //TODO: null자리에 sellerId 넣기(아직 sellerClient 안만들었음)
        TokenResponse tokens = issueTokenUseCase.execute(ctx.userId(),ctx.role(), null);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());

    }

    //토큰 재발급
    public LoginResponse reissue(String oldRefreshToken) {
        TokenResponse tokens = reissueTokenUseCase.execute(oldRefreshToken);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //로그아웃
    public void logout(String refreshToken){
        logoutUseCase.execute(refreshToken);
    }

    //판매자 등록
    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        TokenResponse tokens = issueTokenUseCase.execute(userId, "SELLER", sellerId);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

}
