package com.mossy.boundedContext.product.app.command;

import com.mossy.boundedContext.product.domain.Product;
import com.mossy.boundedContext.product.domain.ProductItem;
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
public class DecreaseStockUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public void execute(List<StockCheckRequest> requests) {
        // 데드락 방지를 위해 ProductItemId 기준으로 오름차순 정렬
        List<StockCheckRequest> sortedRequests = requests.stream()
                .sorted(Comparator.comparing(StockCheckRequest::productItemId))
                .toList();

        for (StockCheckRequest request : sortedRequests) {

            Product product = productRepository.findByProductItemIdWithItems(request.productItemId())
                    .orElseThrow(() -> new DomainException(ErrorCode.PRODUCT_NOT_FOUND));

            product.decreaseItemStock(request.productItemId(), request.quantity());
        }
    }
}
