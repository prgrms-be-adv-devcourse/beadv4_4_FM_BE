package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.domain.CatalogImage;
import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.rest.dto.response.ProductDetailResponse;
import com.mossy.boundedContext.product.out.persistence.mapper.ProductMapper;
import com.mossy.shared.product.enums.ProductItemStatus;
import com.mossy.shared.product.enums.ProductStatus;
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
import static com.mossy.boundedContext.product.domain.QProductItem.productItem;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final ProductMapper productMapper;

    @Override
    public ProductDetailResponse findProductDetail(Long catalogProductId) {

        // 최저가 기준 product id만 추출
        Long mainProductId = queryFactory
                .select(product.id)
                .from(product)
                .join(product.productItems, productItem)
                .where(
                        product.catalogProductId.eq(catalogProductId),
                        product.status.in(ProductStatus.FOR_SALE, ProductStatus.PRE_ORDER),
                        productItem.status.in(ProductItemStatus.ON_SALE, ProductItemStatus.PRE_ORDER)
                )
                .groupBy(product.id)
                .orderBy(
                        productItem.totalPrice.min().asc(),
                        product.salesCount.desc(),
                        product.id.desc()
                )
                .fetchFirst();

        if (mainProductId == null) return null;

        //해당 product의 productItems 전체 fetchJoin
        Product mainProductEntity = queryFactory
                .selectFrom(product)
                .leftJoin(product.productItems, productItem)
                .fetchJoin()
                .where(product.id.eq(mainProductId))
                .fetchOne();

        // 카탈로그 정보 조회
        Tuple catalogInfo = queryFactory
                .select(catalogProduct, category.id, category.name)
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
        Long categoryId = catalogInfo.get(category.id);
        String categoryName = catalogInfo.get(category.name);

        // 다른 판매자들 조회 (위에서 뽑힌 메인 상품 제외)
        List<ProductDetailResponse.OtherSellerDto> otherSellers = queryFactory
                .select(Projections.constructor(ProductDetailResponse.OtherSellerDto.class,
                        product.id,
                        product.sellerId,
                        productItem.totalPrice.min()))  // 최저 totalPrice 추가
                .from(product)
                .join(product.productItems, productItem)
                .on(productItem.status.in(ProductItemStatus.ON_SALE, ProductItemStatus.PRE_ORDER))
                .where(
                        product.catalogProductId.eq(catalogProductId),
                        product.id.ne(mainProductEntity.getId()),
                        product.status.in(ProductStatus.FOR_SALE, ProductStatus.PRE_ORDER)
                )
                .groupBy(product.id, product.sellerId, product.basePrice)
                .fetch();

        // DTO
        return new ProductDetailResponse(
                productMapper.toCatalogDto(
                        catalogEntity,
                        categoryId,
                        categoryName,
                        images),
                productMapper.toProductDto(mainProductEntity),
                otherSellers
        );
    }
}
