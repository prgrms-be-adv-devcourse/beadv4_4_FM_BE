package backend.mossy.shared.market.dto.requets;

public record CartItemUpdateRequest(
        Long productId,
        int quantity
) {
}