package com.mossy.boundedContext.payment.out;

import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "market", url = "${mossy.feign.market-url}")
public interface MarketFeignClient {

    @GetMapping("/internal/orders/{orderId}")
    MarketOrderResponse getOrder(@PathVariable("orderId") Long orderId);

    @GetMapping("/internal/orders/by-order-no/{orderNo}")
    MarketOrderResponse getOrderByOrderNo(@PathVariable("orderNo") String orderNo);
}
