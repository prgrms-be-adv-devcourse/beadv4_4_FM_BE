package com.mossy.boundedContext.payout.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 부하 테스트용 배치 수동 트리거 API (non-prod 전용)
 *
 * POST /internal/payout/batch/collect  → collect + complete 배치 즉시 실행
 * POST /internal/payout/batch/credit   → walletCredit 배치 즉시 실행
 */
@Slf4j
@Profile("!prod")
@RestController
@RequestMapping("/internal/payout/batch")
@RequiredArgsConstructor
public class PayoutAdminController {

    private final JobLauncher jobLauncher;
    private final Job payoutCollectItemsAndCompletePayoutsJob;
    private final Job payoutDailyWalletCreditJob;

    /**
     * 정산 항목 수집 + 정산 완료 배치 수동 실행
     * k6 배치 타이밍 측정 / 락 경합 테스트에 사용
     */
    @PostMapping("/collect")
    public ResponseEntity<Map<String, Object>> triggerCollectAndComplete() throws Exception {
        JobParameters params = buildParams();
        long start = System.currentTimeMillis();

        JobExecution execution = jobLauncher.run(payoutCollectItemsAndCompletePayoutsJob, params);

        long elapsedMs = System.currentTimeMillis() - start;
        log.info("[Admin Batch] collect+complete 완료 - status={}, elapsedMs={}", execution.getStatus(), elapsedMs);

        return ResponseEntity.ok(Map.of(
                "jobName", "payoutCollectItemsAndCompletePayoutsJob",
                "status", execution.getStatus().toString(),
                "exitCode", execution.getExitStatus().getExitCode(),
                "elapsedMs", elapsedMs
        ));
    }

    /**
     * 판매자 지갑 지급 배치 수동 실행
     */
    @PostMapping("/credit")
    public ResponseEntity<Map<String, Object>> triggerWalletCredit() throws Exception {
        JobParameters params = buildParams();
        long start = System.currentTimeMillis();

        JobExecution execution = jobLauncher.run(payoutDailyWalletCreditJob, params);

        long elapsedMs = System.currentTimeMillis() - start;
        log.info("[Admin Batch] walletCredit 완료 - status={}, elapsedMs={}", execution.getStatus(), elapsedMs);

        return ResponseEntity.ok(Map.of(
                "jobName", "payoutDailyWalletCreditJob",
                "status", execution.getStatus().toString(),
                "exitCode", execution.getExitStatus().getExitCode(),
                "elapsedMs", elapsedMs
        ));
    }

    private JobParameters buildParams() {
        return new JobParametersBuilder()
                .addString("runDateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .toJobParameters();
    }
}
