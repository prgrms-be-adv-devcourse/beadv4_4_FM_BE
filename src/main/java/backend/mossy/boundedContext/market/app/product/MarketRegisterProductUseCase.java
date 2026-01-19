package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.Category;
import backend.mossy.boundedContext.market.domain.MarketSeller;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.out.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.ProductRepository;
import backend.mossy.boundedContext.market.out.categoryRepository;
import backend.mossy.shared.market.dto.request.ProductCreateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketRegisterProductUseCase {
    private final ProductRepository productRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final categoryRepository categoryRepository;

    @Transactional
    public Product register(ProductCreateRequest request) {

        MarketSeller seller = marketSellerRepository.findById(request.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자가 아닙니다. ID: " + request.sellerId()));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        Product product = request.toEntity(seller, category);

        // 이미지 URL 리스트 처리 부분

        return productRepository.save(product);
    }
}
