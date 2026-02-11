package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Long> {

    /**
     * 카탈로그와 연관된 카테고리 정보를 한 번의 쿼리로 조회 (N+1 방지)
     */
    @Query("SELECT cp FROM CatalogProduct cp " +
            "JOIN FETCH cp.category " +
            "WHERE cp.id = :id")
    Optional<CatalogProduct> findByIdWithCategory(@Param("id") Long id);

    /**
     * (옵션) 특정 카테고리에 속한 모든 카탈로그를 조회할 때도 유용합니다.
     */
    @Query("SELECT cp FROM CatalogProduct cp " +
            "JOIN FETCH cp.category " +
            "WHERE cp.category.id = :categoryId")
    List<CatalogProduct> findAllByCategoryIdWithCategory(@Param("categoryId") Long categoryId);
}