package com.mossy.member.app.product;

import com.mossy.member.domain.product.ProductDocument;
import com.mossy.member.out.product.ProductElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchUseCase {
    private final ProductElasticRepository esRepository;

    public Page<ProductDocument> findByName(String name, Pageable pageable) {
        return esRepository.findByName(name, pageable);
    }
}
