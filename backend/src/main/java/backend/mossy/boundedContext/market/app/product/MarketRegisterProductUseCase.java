package backend.mossy.boundedContext.market.app.product;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.product.Category;
import backend.mossy.boundedContext.market.domain.product.Product;
import backend.mossy.boundedContext.market.domain.product.event.ProductRegisteredEvent;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.product.ProductRepository;
import backend.mossy.boundedContext.market.out.product.CategoryRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.request.ProductCreateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketRegisterProductUseCase {
    private final ProductRepository productRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;
    private final S3Service s3Service;

    @Transactional
    public Product register(ProductCreateRequest request) {

        MarketSeller seller = marketSellerRepository.findById(request.sellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자가 아닙니다. ID: " + request.sellerId()));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));


        List<String> imageUrls = s3Service.uploadFiles(request.images());

        Product product = request.toEntity(seller, category);
        product.addImages(imageUrls);

        productRepository.save(product);
        eventPublisher.publish(new ProductRegisteredEvent(product.getId()));

        return product;
    }
}
