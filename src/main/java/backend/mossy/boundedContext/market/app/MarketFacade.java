package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.shared.market.dto.requets.ProductRequest;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.MarketUserDto;
import backend.mossy.shared.member.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketGetProductListUseCase marketGetProductListUseCase;
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final MarketCreateCartUseCase marketCreateCartUseCase;
    private final MarketRegisterProductUseCase marketRegisterProductUseCase;

    @Transactional(readOnly = true)
    public List<Product> getProductList() {
        return marketGetProductListUseCase.getProductList();
    }

    @Transactional
    public MarketUser syncUser(UserDto user) {
        return marketSyncUserUseCase.syncUser(user);
    }

    @Transactional
    public RsData<Cart> createCart(MarketUserDto buyer) {
        return marketCreateCartUseCase.createCart(buyer);
    }

    @Transactional
    public Product registerProduct(ProductRequest request) {
        return marketRegisterProductUseCase.register(request);
    }
}
