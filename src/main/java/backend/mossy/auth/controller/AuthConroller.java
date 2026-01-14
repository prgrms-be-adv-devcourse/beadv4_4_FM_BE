package backend.mossy.auth.controller;

import backend.mossy.auth.dto.LoginRequest;
import backend.mossy.auth.dto.LoginResponse;
import backend.mossy.auth.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[TEMP] Auth", description = "임시 인증 API (개발용) - 최종 문서 제출 전 제거/변경 예정")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthConroller {

    private final JwtProvider jwtProvider;

    @Operation(
            summary = "[TEMP] 로그인",
            description = "개발 중 테스트용 로그인 API, 최종 문서 제출 전 제거/변경 예정",
            deprecated = true
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        //TODO: 나중에 DB 검증
        Long mockUserId = 1L;
        String role = "USER";

        String accessToken = jwtProvider.createToken(mockUserId, role);
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }



}
