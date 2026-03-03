package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.in.internal.dto.request.StockCheckRequest;
import com.mossy.boundedContext.product.out.persistence.ProductRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncreaseStockUsecase {

    private final ProductRepository productRepository;

    @Transactional
    public void execute(List<StockCheckRequest> requests) {
        List<Long> itemIds = requests.stream()
                .map(StockCheckRequest::productItemId)
                .sorted()
                .toList();

        // Product들 일괄 조회 (Pessimistic Lock)
        List<Product> products = productRepository.findAllByProductItemIdsWithLock(itemIds);

        for (StockCheckRequest request : requests) {
            Product product = products.stream()
                    .filter(p -> p.hasItem(request.productItemId()))
                    .findFirst()
                    .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

            product.increaseItemStock(request.productItemId(), request.quantity());
        }
    }
}
