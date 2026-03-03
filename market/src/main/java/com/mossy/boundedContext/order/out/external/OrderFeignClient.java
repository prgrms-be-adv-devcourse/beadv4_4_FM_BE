package com.mossy.boundedContext.order.out.external;

import com.mossy.boundedContext.order.out.external.dto.request.StockCheckRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "order-product", url = "${mossy.feign.product-url}")
public interface OrderFeignClient {

    @PostMapping("/internal/v1/products/decrease")
    void decreaseStock(@RequestBody List<StockCheckRequest> items);

    @PostMapping("/internal/v1/products/increase")
    void increaseStock(@RequestBody List<StockCheckRequest> items);
}
