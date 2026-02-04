package com.mossy.member.out.product;


import com.mossy.member.domain.product.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductDocument, Long> {
    Page<ProductDocument> findByName(String name, Pageable pageable);
}
