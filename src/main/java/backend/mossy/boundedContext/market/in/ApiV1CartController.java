package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.requets.CartItemAddRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ApiV1CartController {
    private final MarketFacade marketFacade;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public RsData<Void> addCartItem(
            @RequestParam Long userId,
            @RequestBody @Valid CartItemAddRequest request
    ) {
        return marketFacade.addCartItem(userId, request);
    }
}