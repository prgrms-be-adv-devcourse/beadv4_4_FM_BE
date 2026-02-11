package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.dto.command.CatalogSummary;
import com.mossy.shared.market.enums.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.category " +
            "WHERE p.id = :productId")
    Optional<Product> findById(@Param("productId") Long productId);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.status = :status")
    List<Product> findAllWithCategoryAndImages(@Param("status") ProductStatus status);

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(Long id);

    // ProductRepository 예시
    @Query("select p from Product p " +
            "join fetch p.seller " +
            "join fetch p.catalogProduct cp " +
            "join fetch cp.category " +
            "join fetch p.items " + // ProductItem까지 한 번에 가져옴
            "where p.id = :productId")
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT p.catalogProduct.id FROM Product p WHERE p.id = :productId")
    Optional<Long> findCatalogIdByProductId(@Param("productId") Long productId);

    @Query("SELECT new com.mossy.boundedContext.product.in.dto.command.CatalogSummary(" +
            "MIN(p.basePrice + i.additionalPrice), " +
            "COUNT(DISTINCT p.seller.id)) " +
            "FROM Product p JOIN p.items i " +
            "WHERE p.catalogProduct.id = :catalogId " +
            "AND p.status = 'FOR_SALE'")
    CatalogSummary getCatalogSummary(@Param("catalogId") Long catalogId);

}

