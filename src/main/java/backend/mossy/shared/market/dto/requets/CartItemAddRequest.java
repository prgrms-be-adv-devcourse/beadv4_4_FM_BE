package backend.mossy.shared.market.dto.requets;

public record CartItemAddRequest(
        Long productId,
        int quantity
) {
}