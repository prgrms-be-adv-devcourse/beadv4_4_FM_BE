package com.mossy.infra.batch;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ConfirmPurchasedOrdersBatchJobConfig {

    private final OrderRepository orderRepository;
    private final OutboxPublisher outboxPublisher;

    @Value("${batch.confirm-orders.chunk-size}")
    private int chunkSize;

    @Bean
    public Job confirmPurchasedOrdersJob(JobRepository jobRepository, Step confirmPurchasedOrdersStep) {
        return new JobBuilder("confirmPurchasedOrdersJob", jobRepository)
                .start(confirmPurchasedOrdersStep)
                .build();
    }

    @Bean
    public Step confirmPurchasedOrdersStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        RepositoryItemReader<Order> paidOrdersReader
    ) {
        return new StepBuilder("confirmPurchasedOrdersStep", jobRepository)
                .<Order, Order>chunk(chunkSize, transactionManager)
                .reader(paidOrdersReader)
                .processor(confirmOrderProcessor())
                .writer(confirmOrderWriter())
                .build();
    }

    // threshold는 중복 처리 방지용
    // 배치 중단 후 다시 실행될 때의 시점을 가져와서 이어서 작업하기 위함
    @Bean
    @StepScope
    public RepositoryItemReader<Order> paidOrdersReader(
        @Value("#{jobParameters['threshold']}") LocalDateTime threshold
    ) {
        return new RepositoryItemReaderBuilder<Order>()
                .name("paidOrdersReader")
                .repository(orderRepository)
                .methodName("findPaidOrdersUpdatedBefore")
                .arguments(List.of(threshold))
                .sorts(Map.of("updatedAt", Sort.Direction.ASC))
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public ItemProcessor<Order, Order> confirmOrderProcessor() {
        return order -> {
            order.confirm();
            return order;
        };
    }

    @Bean
    public ItemWriter<Order> confirmOrderWriter() {
        return chunk -> {
            List<? extends Order> orders = chunk.getItems();

            // 정산 이벤트를 아웃박스에 저장
            for (Order order : orders) {
                List<OrderPurchaseConfirmedEvent.OrderItemPayload> eventOrderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderPurchaseConfirmedEvent.OrderItemPayload(
                            orderItem.getId(),
                            orderItem.getSellerId(),
                            orderItem.getProductItemId(),
                            orderItem.getUserCouponId(),
                            orderItem.getCouponType(),
                            orderItem.getWeight(),
                            orderItem.getFinalPrice(),
                            orderItem.getOriginalPrice(),
                            orderItem.getDiscountAmount()
                    ))
                    .toList();

                outboxPublisher.saveEvent(
                    KafkaTopics.ORDER_PURCHASE_CONFIRMED,
                    "Order",
                    order.getId(),
                    OrderPurchaseConfirmedEvent.class.getSimpleName(),
                    new OrderPurchaseConfirmedEvent(
                            order.getId(),
                            order.getBuyer().getId(),
                            order.getPaidAt(),
                            eventOrderItems
                    )
                );
            }
        };
    }
}