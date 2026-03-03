package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.AuthFacade;
import com.mossy.boundedContext.in.dto.request.LoginRequest;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.exception.ErrorCode;
import com.mossy.exception.SuccessCode;
import com.mossy.exception.DomainException;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;

@Slf4j
@Tag(name = "Auth", description = "임시 인증 API (개발용)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {

    private final AuthFacade authFacade;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 , 토큰 발급")
    @PostMapping("/login")
    public RsData<LoginResponse> login(@RequestBody LoginRequest request) {
        return RsData.success(SuccessCode.LOGIN_COMPLETE, authFacade.login(request));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token으로 Access Token 재발급"
    )
    @PostMapping("/reissue")
    public RsData<LoginResponse> reissue(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new DomainException(ErrorCode.MISSING_REFRESH_TOKEN);
        }
        return RsData.success(SuccessCode.REISSUE_COMPLETE, authFacade.reissue(refreshToken));
    }

    @Operation(
            summary = "로그아웃",
            description = "Refresh Token 삭제 및 로그아웃 처리"
    )
    @PostMapping("/logout")
    public RsData<Void> logout(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new DomainException(ErrorCode.MISSING_REFRESH_TOKEN);
        }
        authFacade.logout(refreshToken);
        return RsData.success(SuccessCode.LOGOUT_COMPLETE, null);
    }

    @Operation(
            summary = "인증 Ping",
            description = "서버 및 인증 API 상태 확인용 Ping API"
    )
    @GetMapping("/ping")
    public RsData<String> ping() {
        return RsData.success(SuccessCode.OK, "pong");
    }

    @Operation(
            summary = "판매자 승인 후 토큰 발급",
            description = "판매자 승인 후 SELLER 권한 토큰 발급"
    )
    @PostMapping("/seller-approved")
    public RsData<LoginResponse> issueForSellerApproved(
            @RequestParam Long userId,
            @RequestParam Long sellerId) {
        return RsData.success(SuccessCode.LOGIN_COMPLETE,
                authFacade.issueForSellerApproved(userId, sellerId));
    }

    @Operation(
            summary = "OAuth2 인증 에러 핸들러",
            description = "OAuth2 인증 실패 시 에러 응답"
    )
    @GetMapping("/error")
    public RsData<String> handleOAuth2Error(@RequestParam(required = false) String error,
                                            @RequestParam(required = false) String error_description) {
        String errorMsg = "OAuth2 인증에 실패했습니다";
        if (error_description != null) {
            errorMsg += ": " + error_description;
        } else if (error != null) {
            errorMsg += ": " + error;
        }
        log.warn("OAuth2 인증 에러: error={}, description={}", error, error_description);
        return RsData.fail(ErrorCode.INVALID_CREDENTIALS, errorMsg);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.debug("요청에 쿠키가 없습니다.");
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                log.debug("쿠키에서 RefreshToken 추출 성공");
                return cookie.getValue();
            }
        }

        log.debug("refreshToken 쿠키를 찾을 수 없습니다.");
        return null;
    }
}
