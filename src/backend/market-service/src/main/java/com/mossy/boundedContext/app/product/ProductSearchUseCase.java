package com.mossy.boundedContext.app.product;

import com.mossy.boundedContext.domain.product.ProductDocument;
import com.mossy.boundedContext.out.product.ProductElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchUseCase {
    private final ProductElasticRepository esRepository;

    public Page<ProductDocument> findByName(String name, Pageable pageable) {
        return esRepository.findByName(name, pageable);
    }
}
