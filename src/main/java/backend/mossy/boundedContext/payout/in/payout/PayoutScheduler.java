package backend.mossy.boundedContext.payout.in.payout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class PayoutScheduler {
    private final JobLauncher jobLauncher;
    private final Job payoutCollectItemsAndCompletePayoutsJob;

    /**
     * 배치 실행 중 여부를 나타내는 플래그
     * AtomicBoolean을 사용하여 Thread-safe하게 동시 실행을 방지
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    // 매일 01:00 (KST)
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void runAt01() {
        log.info("=== [정산 배치 스케줄러] 01:00 실행 시작 ===");
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 04:00 (KST)
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runAt04() {
        log.info("=== [정산 배치 스케줄러] 04:00 실행 시작 ===");
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 22:00 (KST)
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void runAt22() {
        log.info("=== [정산 배치 스케줄러] 22:00 실행 시작 ===");
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    /**
     * 정산 항목 수집 및 정산 완료 배치 Job을 실행합니다.
     * AtomicBoolean을 사용하여 동시 실행을 방지하고,
     * 예외가 발생해도 스케줄러가 중단되지 않도록 try-catch로 처리합니다.
     */
    private void runCollectItemsAndCompletePayoutsBatchJob() {
        // CAS(Compare-And-Set) 연산으로 동시 실행 방지
        // false -> true로 변경이 성공하면 Lock 획득, 실패하면 이미 실행 중
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("[정산 배치 건너뜀] 이미 실행 중인 배치가 있습니다. 다음 스케줄까지 대기합니다.");
            return;
        }

        try {
            // Job의 유일성을 보장하기 위해 실행 시각을 파라미터로 추가
            // Spring Batch는 동일한 JobParameters로는 재실행되지 않으므로,
            // 매번 다른 파라미터를 전달하여 스케줄러가 실행될 때마다 새로운 Job Instance가 생성되도록 함
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString(
                            "runDateTime",
                            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                    .toJobParameters();

            log.info("[정산 배치 Job 시작] Parameters: {}", jobParameters);
            JobExecution execution = jobLauncher.run(payoutCollectItemsAndCompletePayoutsJob, jobParameters);

            log.info("[정산 배치 Job 완료] Status: {}, ExitStatus: {}, Duration: {}ms",
                    execution.getStatus(),
                    execution.getExitStatus(),
                    execution.getEndTime().getTime() - execution.getStartTime().getTime());

        } catch (Exception e) {
            log.error("[정산 배치 Job 실패] 배치 실행 중 오류가 발생했습니다.", e);
            // 예외를 다시 던지지 않아 스케줄러가 계속 동작하도록 함
            // 필요시 알림 메커니즘 추가 (예: Slack, 이메일 등)
        } finally {
            // 반드시 Lock 해제 (예외 발생 여부와 관계없이)
            isRunning.set(false);
            log.debug("[정산 배치 Lock 해제] 다음 배치 실행 가능");
        }
    }
}