package backend.mossy.shared.member.dto.request;

public record SellerApproveResponse(
        Long sellerId,
        String accessToken,
        String refreshToken
) {
}
