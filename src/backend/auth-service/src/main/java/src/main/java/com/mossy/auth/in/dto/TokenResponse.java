package src.main.java.com.mossy.auth.in.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
