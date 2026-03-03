package com.mossy.infra.scheduler;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.market.event.OrderStockReturnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final JobLauncher jobLauncher;
    private final Job confirmPurchasedOrdersJob;
    private final OutboxPublisher outboxPublisher;

    public OrderScheduler(
        OrderRepository orderRepository,
        JobLauncher jobLauncher,
        @Qualifier("confirmPurchasedOrdersJob") Job confirmPurchasedOrdersJob,
        OutboxPublisher outboxPublisher
    ) {
        this.orderRepository = orderRepository;
        this.jobLauncher = jobLauncher;
        this.confirmPurchasedOrdersJob = confirmPurchasedOrdersJob;
        this.outboxPublisher = outboxPublisher;
    }

    // 10분마다 실행
    // 주문 생성 후 30분이 지나도 PENDING인 주문은 EXPIRED로 업데이트
    // 재고 복구를 위한 이벤트를 아웃박스에 저장
    @Transactional
    @Scheduled(cron = "0 */15 * * * *")
    public void updateExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<Order> expiredOrders = orderRepository.findPendingOrdersCreatedBefore(threshold);

        for (Order order : expiredOrders) {
            order.expire();
            List<OrderStockReturnEvent.OrderItemStock> orderItemStocks = order.getOrderItems().stream()
                    .map(orderItem -> new OrderStockReturnEvent.OrderItemStock(
                            orderItem.getProductItemId(),
                            orderItem.getQuantity()
                    ))
                    .toList();

            outboxPublisher.saveEvent(
                    KafkaTopics.ORDER_STOCK_RETURN,
                    "Order",
                    order.getId(),
                    OrderStockReturnEvent.class.getSimpleName(),
                    new OrderStockReturnEvent(orderItemStocks)
            );

            log.info("주문 만료 처리 완료 - orderId: {}, 재고 복구 이벤트 저장", order.getId());
        }
    }

    // 매일 자정에 실행
    // 결제되지 않은 주문을 일주일 뒤 삭제
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
        orderRepository.deleteExpiredOrders(threshold);
    }

    // 매일 새벽 3시 실행
    // PAID 상태에서 일주일이 지난 주문을 CONFIRMED로 변경 및 아웃박스 저장
    @Scheduled(cron = "0 0 3 * * *")
    public void confirmAndSavePayoutEvents() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("runAt", LocalDateTime.now())
                    .addLocalDateTime("threshold", threshold)
                    .toJobParameters();

            jobLauncher.run(confirmPurchasedOrdersJob, params);
        } catch (Exception e) {
            log.error("구매확정 및 아웃박스 저장 배치 실행 실패: {}", e.getMessage(), e);
        }
    }
}
