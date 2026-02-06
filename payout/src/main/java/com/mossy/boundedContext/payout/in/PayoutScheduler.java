package com.mossy.boundedContext.payout.in;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class PayoutScheduler {
    private final JobLauncher jobLauncher;
    private final Job payoutCollectItemsAndCompletePayoutsJob;

    // 매일 01:00 (KST)
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void runAt01() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 04:00 (KST)
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runAt04() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 22:00 (KST)
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void runAt22() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    private void runCollectItemsAndCompletePayoutsBatchJob() throws JobInstanceAlreadyCompleteException,
            JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(
                        "runDateTime",
                        LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                .toJobParameters();

        JobExecution execution = jobLauncher.run(payoutCollectItemsAndCompletePayoutsJob, jobParameters);
    }
}