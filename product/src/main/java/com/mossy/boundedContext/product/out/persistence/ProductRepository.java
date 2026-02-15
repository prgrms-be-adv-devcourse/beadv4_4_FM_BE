package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.out.CatalogSummaryDto;
import com.mossy.boundedContext.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    // 카탈로그에 속한 모든 판매자의 모든 단품 중 최저가 계산
    // basePrice + additionalPrice 중 가장 낮은 값
    @Query("SELECT MIN(p.basePrice + pi.additionalPrice) " +
            "FROM Product p " +
            "LEFT JOIN p.productItems pi " +
            "WHERE p.catalogProductId = :catalogId " +
            "AND p.status = 'FOR_SALE'")
    BigDecimal findMinPriceByCatalogId(@Param("catalogId") Long catalogId);

    // 해당 카탈로그를 판매 중인 판매자 수
    @Query("SELECT COUNT(DISTINCT p.sellerId) " +
            "FROM Product p " +
            "WHERE p.catalogProductId = :catalogId AND p.status = 'FOR_SALE'")
    Long countSellersByCatalogId(@Param("catalogId") Long catalogId);

    @Query("SELECT p.catalogProductId as catalogId, " +
            "MIN(p.basePrice) as minPrice, " +
            "COUNT(p.id) as sellerCount " +
            "FROM Product p " +
            "WHERE p.status = 'FOR_SALE' " +
            "GROUP BY p.catalogProductId")
    List<CatalogSummaryDto> findAllCatalogSummaries();
}

