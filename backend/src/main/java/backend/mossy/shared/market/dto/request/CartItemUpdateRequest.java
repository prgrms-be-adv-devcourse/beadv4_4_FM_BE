package backend.mossy.shared.market.dto.request;

public record CartItemUpdateRequest(
        Long productId,
        int quantity
) {
}