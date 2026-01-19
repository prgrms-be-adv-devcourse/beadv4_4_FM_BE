package backend.mossy.boundedContext.auth.in;

import backend.mossy.boundedContext.auth.app.AuthFacade;
import backend.mossy.boundedContext.auth.in.dto.LoginRequest;
import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.shared.member.dto.request.SignupRequest;
import backend.mossy.boundedContext.member.app.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[TEMP] Auth", description = "임시 인증 API (개발용)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    private final UserFacade userFacade;

    @Operation(summary = "회원가입", description = "일반 유저(USER)로 가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        Long userId = userFacade.signup(request);
        return ResponseEntity.ok("회원가입 성공! User ID: " + userId);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authFacade.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token으로 Access Token 갱신"
    )
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        LoginResponse response = authFacade.reissue(refreshToken);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그아웃",
            description = "Refresh Token 삭제"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("RefreshToken") String refreshToken) {
        authFacade.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

}
