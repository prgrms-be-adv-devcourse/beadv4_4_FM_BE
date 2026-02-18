package com.mossy.boundedContext.catalog.out;

import com.mossy.boundedContext.catalog.query.CatalogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogSearchRepository extends ElasticsearchRepository<CatalogDocument, Long> {

    Page<CatalogDocument> findByName(String name, Pageable pageable);
}
