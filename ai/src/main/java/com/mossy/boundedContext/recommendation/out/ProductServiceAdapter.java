package com.mossy.boundedContext.recommendation.out;

import com.mossy.boundedContext.recommendation.out.external.ProductFeignClient;
import com.mossy.boundedContext.recommendation.out.external.dto.response.ProductResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductServiceAdapter {

    private final ProductFeignClient feignClient;

    public Mono<List<ProductResponse>> getProductDetails(List<Long> productIds) {
        return Mono.fromCallable(() -> feignClient.getProductDetails(productIds))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(e -> new DomainException(ErrorCode.FEIGN_CALL_FAILED));
    }

    public Mono<List<ProductResponse>> getProductsFilteredByReviews(List<Long> productIds) {
        return Mono.fromCallable(() -> feignClient.filterByReviews(productIds))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorMap(e -> new DomainException(ErrorCode.FEIGN_CALL_FAILED));
    }
}
