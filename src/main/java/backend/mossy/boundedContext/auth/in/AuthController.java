package backend.mossy.boundedContext.auth.in;

import backend.mossy.boundedContext.auth.app.AuthFacade;
import backend.mossy.boundedContext.auth.in.dto.LoginRequest;
import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.member.dto.request.SignupRequest;
import backend.mossy.boundedContext.member.app.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[TEMP] Auth", description = "임시 인증 API (개발용)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final UserFacade userFacade;

    @Operation(summary = "회원가입", description = "일반 유저(USER)로 가입합니다.")
    @PostMapping("/signup")
    public RsData<Long> signup(@RequestBody SignupRequest request) {
        return RsData.success("회원가입 성공", userFacade.signup(request));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public RsData<LoginResponse> login(@RequestBody LoginRequest request) {
        return RsData.success("로그인 성공", authFacade.login(request));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token으로 Access Token 갱신"
    )
    @PostMapping("/reissue")
    public RsData<LoginResponse> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return RsData.success("토큰 재발급 성공",authFacade.reissue(refreshToken));

    }

    @Operation(
            summary = "로그아웃",
            description = "Refresh Token 삭제"
    )
    @PostMapping("/logout")
    public RsData<Void> logout(@RequestHeader("RefreshToken") String refreshToken) {
        authFacade.logout(refreshToken);
        return RsData.success("로그아웃 성공");
    }

}
