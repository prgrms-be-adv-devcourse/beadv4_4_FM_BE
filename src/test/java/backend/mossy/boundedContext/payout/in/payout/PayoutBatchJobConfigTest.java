package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class PayoutBatchJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // 배치를 실행해주는 표준 유틸리티

    @MockitoBean
    private PayoutFacade payoutFacade;

    @Test
    @DisplayName("성공: 전체 정산 배치 Job이 정상 실행된다")
    void payoutJob_Success() throws Exception {
        // Given
        when(payoutFacade.collectPayoutItemsMore(anyInt()))
                .thenReturn(RsData.success("수집 성공", 10));
        when(payoutFacade.completePayoutsMore(anyInt()))
                .thenReturn(RsData.success("완료 성공", 5));

        // 중복 실행 방지를 위한 파라미터
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When: launchJob 메서드는 내부적으로 주입된 Job을 찾아 실행합니다.
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(payoutFacade, times(1)).collectPayoutItemsMore(anyInt());
        verify(payoutFacade, times(1)).completePayoutsMore(anyInt());
    }

    @Test
    @DisplayName("실패: Step 1 오류 시 Job은 실패한다")
    void payoutJob_Fail() throws Exception {
        // Given
        when(payoutFacade.collectPayoutItemsMore(anyInt()))
                .thenThrow(new RuntimeException("Error"));

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
        verify(payoutFacade, never()).completePayoutsMore(anyInt());
    }
}