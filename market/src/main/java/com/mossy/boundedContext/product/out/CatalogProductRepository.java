package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Long> {

}