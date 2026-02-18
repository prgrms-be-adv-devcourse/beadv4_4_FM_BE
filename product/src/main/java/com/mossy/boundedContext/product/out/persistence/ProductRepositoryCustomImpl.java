package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.mapper.ProductMapper;
import com.mossy.shared.market.enums.ProductStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mossy.boundedContext.catalog.domain.QCatalogImage.catalogImage;
import static com.mossy.boundedContext.catalog.domain.QCatalogProduct.catalogProduct;
import static com.mossy.boundedContext.category.domain.QCategory.category;
import static com.mossy.boundedContext.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final ProductMapper productMapper;

    @Override
    public ProductDetailResponse findProductDetail(Long catalogProductId) {

        // 1. 해당 카탈로그에 속한 상품 중 '최저가'인 상품 하나를 메인으로 선정
        Product mainProductEntity = queryFactory
                .selectFrom(product)
                .where(
                        product.catalogProductId.eq(catalogProductId),
                        product.status.eq(ProductStatus.FOR_SALE)
                )
                .orderBy(
                        product.basePrice.asc(),        // 1순위: 최저가
                        product.salesCount.desc(),      // 2순위: 판매량 많은 순
                        product.id.desc()               // 3순위: 최신 등록 순 (ID가 클수록 최근)
                )
                .limit(1)
                .fetchOne();

        if (mainProductEntity == null) return null;

        // 2. 카탈로그 정보 조회 (CatalogProduct + Category)
        Tuple catalogInfo = queryFactory
                .select(catalogProduct, category.name)
                .from(catalogProduct)
                .join(catalogProduct.category, category)
                .where(catalogProduct.id.eq(catalogProductId))
                .fetchOne();
        // 카탈로그 상품 이미지 리스트
        List<CatalogImage> images = queryFactory
                .selectFrom(catalogImage)
                .where(catalogImage.targetId.eq(catalogProductId))
                .limit(5)
                .fetch();

        CatalogProduct catalogEntity = catalogInfo.get(catalogProduct);
        String categoryName = catalogInfo.get(category.name);

        // 3. 다른 판매자들 조회 (위에서 뽑힌 메인 상품 제외)
        List<ProductDetailResponse.OtherSellerDto> otherSellers = queryFactory
                .select(Projections.constructor(ProductDetailResponse.OtherSellerDto.class,
                        product.id,
                        product.sellerId,
                        product.basePrice))
                .from(product)
                .where(
                        product.catalogProductId.eq(catalogProductId),
                        product.id.ne(mainProductEntity.getId()),
                        product.status.eq(ProductStatus.FOR_SALE)
                )
                .fetch();

        // 4. 최종 DTO 조립
        return new ProductDetailResponse(
                productMapper.toCatalogDto(
                        catalogEntity,
                        categoryName,
                        images),
                productMapper.toProductDto(mainProductEntity),
                otherSellers
        );
    }
}
