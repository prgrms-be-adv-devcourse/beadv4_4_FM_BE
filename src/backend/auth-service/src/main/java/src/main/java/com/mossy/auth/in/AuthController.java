package src.main.java.com.mossy.auth.in;

import com.mossy.auth.app.AppFacade;
import com.mossy.global.rsData.RsData;
import com.mossy.shared.member.dto.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.mossy.auth.in.dto.LoginRequest;
import src.main.java.com.mossy.auth.in.dto.LoginResponse;

@Tag(name = "Auth", description = "임시 인증 API (개발용)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final com.mossy.auth.app.AuthFacade authFacade;
    private final UserFacade userFacade;

    @Operation(
            summary = "회원가입",
            description = "일반 유저(USER)로 가입")
    @PostMapping("/signup")
    public RsData<Long> signup(@RequestBody SignupRequest request) {
        return RsData.success("회원가입 성공", userFacade.signup(request));
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 , 토큰 발급")
    @PostMapping("/login")
    public RsData<LoginResponse> login(@RequestBody LoginRequest request) {
        return RsData.success("로그인 성공", authFacade.login(request));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token으로 Access Token 재발급"
    )
    @PostMapping("/reissue")
    public RsData<LoginResponse> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        return RsData.success("토큰 재발급 성공",authFacade.reissue(refreshToken));

    }

    @Operation(
            summary = "로그아웃",
            description = "Refresh Token 삭제 및 로그아웃 처리"
    )
    @PostMapping("/logout")
    public RsData<Void> logout(@RequestHeader("RefreshToken") String refreshToken) {
        authFacade.logout(refreshToken);
        return RsData.success("로그아웃 성공");
    }

    @Operation(
            summary = "인증 Ping",
            description = "서버 및 인증 API 상태 확인용 Ping API"
    )
    @GetMapping("/ping")
    public RsData<String> ping() {
        return RsData.success("pong", "pong");
    }

}
