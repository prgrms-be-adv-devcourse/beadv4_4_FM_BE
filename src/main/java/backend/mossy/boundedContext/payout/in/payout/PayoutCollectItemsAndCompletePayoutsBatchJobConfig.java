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
    // 한 번에 처리할 데이터 묶음(Chunk)의 크기
    @Value("${batch.payout.chunk-size}")
    private int chunkSize;

    private final PayoutFacade payoutFacade;

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
            Step payoutCompletePayoutsStep
    ) {
        return new JobBuilder("payoutCollectItemsAndCompletePayoutsJob", jobRepository)
                // 1. 첫 번째로 payoutCollectItemsStep 실행
                .start(payoutCollectItemsStep)
                // 2. 그 다음으로 payoutCompletePayoutsStep 실행
                .next(payoutCompletePayoutsStep)
                .build();
    }

    /**
     * [배치 1단계] 정산 항목 수집 Step
     * 정산 대기 기간이 지난 '정산 후보(PayoutCandidateItem)'들을 찾아서
     * 판매자별 '정산(Payout)' 객체에 '정산 항목(PayoutItem)'으로 추가합니다.
     */
    @Bean
    public Step payoutCollectItemsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("payoutCollectItemsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== [정산 항목 수집 Step 시작] ===");
                    int totalProcessed = 0;
                    int loopCount = 0;

                    // 모든 정산 후보 아이템을 처리할 때까지 반복
                    while (true) {
                        loopCount++;
                        log.debug("[정산 항목 수집] {}번째 배치 처리 시작 (청크 크기: {})", loopCount, chunkSize);

                        RsData<Integer> result = payoutFacade.collectPayoutItemsMore(chunkSize);
                        int processedCount = result.getData();

                        if (processedCount == 0) {
                            log.info("[정산 항목 수집] 처리할 데이터가 없습니다. 종료합니다.");
                            break;
                        }

                        totalProcessed += processedCount;
                        contribution.incrementWriteCount(processedCount);
                        log.info("[정산 항목 수집] {}개 항목 처리 완료 (누적: {}개)", processedCount, totalProcessed);
                    }

                    log.info("=== [정산 항목 수집 Step 완료] 총 {}개 항목 처리, {}번 반복 ===", totalProcessed, loopCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    /**
     * [배치 2단계] 정산 완료 Step
     * 1단계에서 항목들이 채워진 '정산(Payout)' 객체들을 실제 정산 처리합니다.
     */
    @Bean
    public Step payoutCompletePayoutsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("payoutCompletePayoutsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("=== [정산 완료 Step 시작] ===");
                    int totalProcessed = 0;
                    int loopCount = 0;

                    // 모든 정산 객체를 처리할 때까지 반복
                    while (true) {
                        loopCount++;
                        log.debug("[정산 완료] {}번째 배치 처리 시작 (청크 크기: {})", loopCount, chunkSize);

                        RsData<Integer> result = payoutFacade.completePayoutsMore(chunkSize);
                        int processedCount = result.getData();

                        if (processedCount == 0) {
                            log.info("[정산 완료] 처리할 데이터가 없습니다. 종료합니다.");
                            break;
                        }

                        totalProcessed += processedCount;
                        contribution.incrementWriteCount(processedCount);
                        log.info("[정산 완료] {}개 정산 완료 (누적: {}개)", processedCount, totalProcessed);
                    }

                    log.info("=== [정산 완료 Step 완료] 총 {}개 정산 완료, {}번 반복 ===", totalProcessed, loopCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}

