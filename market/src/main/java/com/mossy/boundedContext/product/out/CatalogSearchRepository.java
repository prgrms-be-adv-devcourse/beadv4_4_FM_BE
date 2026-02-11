package com.mossy.boundedContext.product.out;

import com.mossy.boundedContext.product.domain.CatalogDocument;
import com.mossy.boundedContext.product.domain.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogSearchRepository extends ElasticsearchRepository<CatalogDocument, Long> {

    Page<ProductDocument> findByName(String name, Pageable pageable);
}
