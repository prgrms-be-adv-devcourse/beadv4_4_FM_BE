package src.main.java.com.mossy.auth.in.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
