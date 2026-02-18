package com.mossy.boundedContext.catalog.out;

import com.mossy.boundedContext.catalog.domain.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Long> {

}