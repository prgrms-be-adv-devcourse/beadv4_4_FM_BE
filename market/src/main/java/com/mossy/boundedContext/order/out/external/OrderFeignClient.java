package com.mossy.boundedContext.order.out.external;

import com.mossy.boundedContext.order.out.external.dto.request.StockCheckRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "order-product", url = "http://localhost:8090")
public interface OrderFeignClient {

    @PostMapping("/internal/v1/orders/products/decrease")
    void decreaseStock(@RequestBody List<StockCheckRequest> items);
}
