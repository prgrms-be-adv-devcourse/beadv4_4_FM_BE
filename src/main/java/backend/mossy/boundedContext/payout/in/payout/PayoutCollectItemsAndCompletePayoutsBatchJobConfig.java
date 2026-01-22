package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PayoutCollectItemsAndCompletePayoutsBatchJobConfig {
    // 한 번의 Step 실행에서 처리할 데이터 묶음(Chunk)의 크기
    private static final int CHUNK_SIZE = 10;

    private final PayoutFacade payoutFacade;

    public PayoutCollectItemsAndCompletePayoutsBatchJobConfig(PayoutFacade payoutFacade) {
        this.payoutFacade = payoutFacade;
    }

    /**
     * 'payoutCollectItemsAndCompletePayoutsJob' 이라는 이름의 Job을 생성합니다.
     * Job은 Step들의 묶음이며, 실행 순서를 정의합니다.ㅌㅈ
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
     */
    @Bean
    public Step payoutCollectItemsStep(JobRepository jobRepository) {
        return new StepBuilder("payoutCollectItemsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // CHUNK_SIZE 만큼 정산 후보를 가져와 PayoutItem으로 변환합니다.
                    int processedCount = payoutFacade.collectPayoutItemsMore(CHUNK_SIZE).getData();

                    // 더 이상 처리할 데이터가 없으면 Step을 종료(FINISHED)합니다.
                    if (processedCount == 0) {
                        return RepeatStatus.FINISHED;
                    }

                    contribution.incrementWriteCount(processedCount);

                    // 처리한 데이터가 있다면, 다음 묶음을 계속 처리하도록(CONTINUABLE) 합니다.
                    return RepeatStatus.CONTINUABLE;
                })
                .build();
    }

    /**
     * [배치 2단계] 정산 완료 Step
     * 1단계에서 항목들이 채워진 '정산(Payout)' 객체들을 실제 정산 처리합니다.
     */
    @Bean
    public Step payoutCompletePayouts(JobRepository jobRepository) {
        return new StepBuilder("payoutCompletePayouts", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // CHUNK_SIZE 만큼 정산할 Payout 객체를 가져와 정산 완료 처리합니다.
                    int processedCount = payoutFacade.completePayoutsMore(CHUNK_SIZE).getData();

                    // 더 이상 처리할 데이터가 없으면 Step을 종료(FINISHED)합니다.
                    if (processedCount == 0) {
                        return RepeatStatus.FINISHED;
                    }

                    contribution.incrementWriteCount(processedCount);

                    // 처리한 데이터가 있다면, 다음 묶음을 계속 처리하도록(CONTINUABLE) 합니다.
                    return RepeatStatus.CONTINUABLE;
                })
                .build();
    }
}

