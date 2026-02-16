package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class PayoutDailyWalletCreditBatchJobConfig {

    private final PayoutFacade payoutFacade;

    public PayoutDailyWalletCreditBatchJobConfig(PayoutFacade payoutFacade) {
        this.payoutFacade = payoutFacade;
    }

    /**
     * 일별 판매자 지갑 지급 Job
     */
    @Bean
    public Job payoutDailyWalletCreditJob(
            JobRepository jobRepository,
            Step payoutDailyWalletCreditStep
    ) {
        return new JobBuilder("payoutDailyWalletCreditJob", jobRepository)
                .start(payoutDailyWalletCreditStep)
                .build();
    }

    /**
     * 정산 완료된 Payout을 조회하여 지갑 입금 처리
     * 각 Payout마다 1:1로 이벤트 발행
     */
    @Bean
    public Step payoutDailyWalletCreditStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        int chunkSize = 100;  // 한 번에 처리할 Payout 수

        return new StepBuilder("payoutDailyWalletCreditStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("[지급 배치] 시작");

                    try {
                        RsData<Integer> result = payoutFacade.processDailyPayoutToWallet(chunkSize);
                        int processedCount = result.getData();

                        if (processedCount > 0) {
                            log.info("[지급 배치] 완료 - 처리된 정산: {}건", processedCount);
                            contribution.incrementWriteCount(processedCount);
                        } else {
                            log.info("[지급 배치] 처리할 정산이 없습니다.");
                        }

                        // 처리할 정산이 더 있으면 반복
                        if (processedCount >= chunkSize) {
                            return RepeatStatus.CONTINUABLE;
                        }

                        return RepeatStatus.FINISHED;

                    } catch (Exception e) {
                        log.error("[지급 배치] 실패 - 에러: {}", e.getMessage(), e);
                        throw e;
                    }
                }, transactionManager)
                .build();
    }
}