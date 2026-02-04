package com.mossy.member.app.product;

import com.mossy.member.out.market.MarketSellerRepository;
import com.mossy.member.out.product.ProductRepository;
import com.mossy.member.out.product.CategoryRepository;
import com.mossy.global.aws.s3.service.S3Service;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketRegisterProductUseCase {
    private final ProductRepository productRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;
    private final S3Adapter s3Adapter;

    @Value("${app.s3.dirs.product:product}") // 기본값 product 설정
    private String productDir;

//    @Transactional
//    public Product register(ProductCreateRequest request) {
//
//        MarketSeller seller = marketSellerRepository.findById(request.sellerId())
//                .orElseThrow(() -> new IllegalArgumentException("판매자가 아닙니다. ID: " + request.sellerId()));
//
//        Category category = categoryRepository.findById(request.categoryId())
//                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
//
//
//        List<String> imageUrls = s3Adapter.uploadFiles(request.images(), productDir);
//
//        Product product = request.toEntity(seller, category);
//        product.addImages(imageUrls);
//
//        productRepository.save(product);
//        eventPublisher.publish(new ProductRegisteredEvent(product.getId()));
//
//        return product;
//    }
}
