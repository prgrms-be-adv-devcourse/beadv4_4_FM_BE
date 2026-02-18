package com.mossy.boundedContext.catalog.out;

import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Long> {

    @Query("select c from CatalogProduct c join fetch c.category")
    List<CatalogProduct> findAllWithCategory();
}