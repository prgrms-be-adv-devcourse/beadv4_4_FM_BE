package backend.mossy.boundedContext.auth.in;

import backend.mossy.boundedContext.auth.app.AuthFacade;
import backend.mossy.boundedContext.auth.in.dto.LoginRequest;
import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[TEMP] Auth", description = "임시 인증 API (개발용)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthConroller {

    private final JwtProvider jwtProvider;
    private final AuthFacade authFacade;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        //TODO: 나중에 DB 검증
        Long mockUserId = 1L;
        String mockRole = "USER";

        return ResponseEntity.ok(authFacade.login(mockUserId, mockRole));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token으로 Access Token 갱싱"
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
