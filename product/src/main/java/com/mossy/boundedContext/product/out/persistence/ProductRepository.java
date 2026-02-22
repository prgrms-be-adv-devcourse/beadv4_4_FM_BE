package com.mossy.boundedContext.product.out.persistence;

import com.mossy.boundedContext.catalog.out.CatalogSummaryDto;
import com.mossy.boundedContext.product.domain.Product;
import com.mossy.shared.product.enums.ProductItemStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    // 카탈로그 id 로 최저가 상품 찾기
    @Query("""
        SELECT
            p.catalogProductId as catalogId,
            MIN(pi.totalPrice) as minPrice,
            COUNT(DISTINCT p.id) as sellerCount,
            (
                SELECT p2.id FROM ProductItem pi2
                JOIN Product p2 ON pi2.productId = p2.id
                WHERE p2.catalogProductId = :catalogId
                    AND pi2.status IN :statuses
                ORDER BY pi2.totalPrice ASC, pi2.id ASC
                LIMIT 1
            ) as minPriceProductId
        FROM Product p
        JOIN p.productItems pi
        WHERE p.catalogProductId = :catalogId
        AND pi.status IN :statuses
        GROUP BY p.catalogProductId
    """)
    Optional<CatalogSummaryDto> findCatalogSummaryByCatalogId(
            @Param("catalogId") Long catalogId,
            @Param("statuses") List<ProductItemStatus> statuses
    );

    // 데이터 초기화시
    @Query("""
        SELECT
            p.catalogProductId as catalogId,
            MIN(pi.totalPrice) as minPrice,
            COUNT(DISTINCT p.id) as sellerCount,
            (
                SELECT p2.id FROM ProductItem pi2
                JOIN Product p2 ON pi2.productId = p2.id
                WHERE p2.catalogProductId = p.catalogProductId
                    AND pi2.status IN :statuses
                ORDER BY pi2.totalPrice ASC, pi2.id ASC
                LIMIT 1
            ) as minPriceProductId
        FROM Product p
        JOIN p.productItems pi
        WHERE pi.status IN :statuses
        GROUP BY p.catalogProductId
    """)
    List<CatalogSummaryDto> findAllCatalogSummaries(
            @Param("statuses") List<ProductItemStatus> statuses);

    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithAllDetails(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Product p
        JOIN FETCH p.productItems i
        WHERE i.id = :productItemId
    """)
    Optional<Product> findByProductItemIdWithItems(@Param("productItemId") Long productItemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT DISTINCT p 
        FROM Product p
        JOIN FETCH p.productItems i
        WHERE i.id IN :itemIds
        ORDER BY p.id ASC
    """)
    List<Product> findAllByProductItemIdsWithLock(@Param("itemIds") List<Long> itemIds);
}

