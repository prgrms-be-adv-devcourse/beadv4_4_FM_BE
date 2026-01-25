package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class PayoutCollectItemsAndCompletePayoutsBatchJobConfig {

    private final PayoutFacade payoutFacade;

    // application.yml에서 배치 청크 크기를 주입받습니다.
    @Value("${batch.payout.chunk-size:100}")
    private int chunkSize;

    public PayoutCollectItemsAndCompletePayoutsBatchJobConfig(PayoutFacade payoutFacade) {
        this.payoutFacade = payoutFacade;
    }

    /**
     * 'payoutCollectItemsAndCompletePayoutsJob' 이라는 이름의 Job을 생성합니다.
     * Job은 Step들의 묶음이며, 실행 순서를 정의합니다.
     */
    @Bean
    public Job payoutCollectItemsAndCompletePayoutsJob(
            JobRepository jobRepository,
            Step payoutCollectItemsStep,
            Step payoutCompletePayouts
    ) {
        return new JobBuilder("payoutCollectItemsAndCompletePayoutsJob", jobRepository)
                // 1. 첫 번째로 payoutCollectItemsStep 실행
                .start(payoutCollectItemsStep)
                // 2. 그 다음으로 payoutCompletePayouts 실행
                .next(payoutCompletePayouts)
                .build();
    }

    /**
     * [배치 1단계] 정산 항목 수집 Step
     * 정산 대기 기간이 지난 '정산 후보(PayoutCandidateItem)'들을 찾아서
     * 판매자별 '정산(Payout)' 객체에 '정산 항목(PayoutItem)'으로 추가합니다.
     *
     * Spring Batch 6.0: TransactionManager 필수
     */
    @Bean
    public Step payoutCollectItemsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("payoutCollectItemsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("[정산 항목 수집] 시작 - 청크 크기: {}", chunkSize);

                    try {
                        // chunkSize 만큼 정산 후보를 가져와 PayoutItem으로 변환합니다.
                        RsData<Integer> result = payoutFacade.collectPayoutItemsMore(chunkSize);
                        int processedCount = result.getData();

                        if (processedCount > 0) {
                            log.info("[정산 항목 수집] 완료 - 처리된 항목: {}건", processedCount);
                            contribution.incrementWriteCount(processedCount);
                        } else {
                            log.info("[정산 항목 수집] 처리할 항목이 없습니다.");
                        }

                        // 한 번만 실행하고 종료 (스케줄러가 주기적으로 다시 호출)
                        return RepeatStatus.FINISHED;

                    } catch (Exception e) {
                        log.error("[정산 항목 수집] 실패 - 에러: {}", e.getMessage(), e);
                        throw e; // 예외를 다시 던져서 Job 실패로 처리
                    }
                }, transactionManager) // Spring Batch 6.0: TransactionManager 필수
                .build();
    }

    /**
     * [배치 2단계] 정산 완료 Step
     * 1단계에서 항목들이 채워진 '정산(Payout)' 객체들을 실제 정산 처리합니다.
     *
     * Spring Batch 6.0: TransactionManager 필수
     */
    @Bean
    public Step payoutCompletePayouts(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("payoutCompletePayouts", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("[정산 완료 처리] 시작 - 청크 크기: {}", chunkSize);

                    try {
                        // chunkSize 만큼 정산할 Payout 객체를 가져와 정산 완료 처리합니다.
                        RsData<Integer> result = payoutFacade.completePayoutsMore(chunkSize);
                        int processedCount = result.getData();

                        if (processedCount > 0) {
                            log.info("[정산 완료 처리] 완료 - 처리된 정산: {}건", processedCount);
                            contribution.incrementWriteCount(processedCount);
                        } else {
                            log.info("[정산 완료 처리] 처리할 정산이 없습니다.");
                        }

                        // 한 번만 실행하고 종료 (스케줄러가 주기적으로 다시 호출)
                        return RepeatStatus.FINISHED;

                    } catch (Exception e) {
                        log.error("[정산 완료 처리] 실패 - 에러: {}", e.getMessage(), e);
                        throw e; // 예외를 다시 던져서 Job 실패로 처리
                    }
                }, transactionManager) // Spring Batch 6.0: TransactionManager 필수
                .build();
    }
}


