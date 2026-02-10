package com.mossy.infra.scheduler;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderExpireDeleteScheduler {

    private final OrderRepository orderRepository;

    // 매일 자정에 실행
    // 결제되지 않은 주문을 일주일 뒤 삭제
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
        orderRepository.deleteExpiredOrders(threshold);
    }

    // 10분마다 실행
    // 주문 생성 후 30분이 지나도 PENDING인 주문은 EXPIRED로 업데이트
    @Transactional
    @Scheduled(cron = "0 */10 * * * *")
    public void updateExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<Order> expiredOrders = orderRepository.findPendingOrdersCreatedBefore(threshold);
        expiredOrders.forEach(Order::expire);
    }
}
