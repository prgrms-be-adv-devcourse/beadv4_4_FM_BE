package com.mossy.boundedContext.order.out.external;

import com.mossy.boundedContext.order.out.external.dto.request.StockCheckRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product", url = "http://localhost:8090")
public interface OrderFeignClient {

    // request가 복잡하여 Post 요청
    @PostMapping("/api/v1/orders/products/stock/validate")
    void validateStock(@RequestBody List<StockCheckRequest> items);
}
