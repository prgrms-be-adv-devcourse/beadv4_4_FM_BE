package backend.mossy.boundedContext.payout.in.payout;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class PayoutScheduler {
    private final JobOperator jobOperator;
    private final Job payoutCollectItemsAndCompletePayoutsJob;

    // 매일 01:00 (KST)
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void runAt01() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 04:00 (KST)
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runAt04() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    // 매일 22:00 (KST)
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void runAt22() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException,
            JobExecutionAlreadyRunningException, JobRestartException {
        runCollectItemsAndCompletePayoutsBatchJob();
    }

    private void runCollectItemsAndCompletePayoutsBatchJob() throws JobInstanceAlreadyCompleteException,
            InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(
                        "runDateTime",
                        LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                .toJobParameters();

        JobExecution execution = jobOperator.start(payoutCollectItemsAndCompletePayoutsJob, jobParameters);
    }
}