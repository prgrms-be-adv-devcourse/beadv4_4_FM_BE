package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.MarketSeller;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.out.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.ProductRepository;
import backend.mossy.shared.market.dto.requets.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketRegisterProductUseCase {
    private final ProductRepository productRepository;
    private final MarketSellerRepository marketSellerRepository;

    @Transactional
    public Product register(ProductRequest request) {

        MarketSeller seller = marketSellerRepository.findById(request.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자가 아닙니다. ID: " + request.sellerId()));

        Product product = request.toEntity(seller);

        // 이미지 URL 리스트 처리 부분

        return productRepository.save(product);
    }
}
