package com.mossy.infra.scheduler;

import com.mossy.boundedContext.order.app.OrderFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderFacade orderFacade;
    private final JobLauncher jobLauncher;
    private final Job confirmPurchasedOrdersJob;

    // 주문 생성 후 15분이 지나도 PENDING인 주문은 EXPIRED로 업데이트
    // 재고 복구를 위한 이벤트를 아웃박스에 저장
    @Scheduled(fixedDelay = 60000)
    public void updateExpiredOrders() {
        orderFacade.expireOrders();
    }

    // 매일 자정에 실행
    // 결제되지 않은 주문을 일주일 뒤 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredOrders() {
        orderFacade.deleteExpiredOrders();
    }

    // 매일 새벽 4시 실행
    // PAID 상태에서 일주일이 지난 주문을 CONFIRMED로 변경 및 아웃박스 저장
    @Scheduled(cron = "${batch.confirm-orders.cron}")
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
