package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // [중요] 최신 버전에서는 이 경로를 사용합니다.
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

// Mockito 및 MockMvc 정적 임포트
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiV1PayoutBatchTestControllerTest {

    @Autowired
    private MockMvc mvc;

    // Spring Boot 3.4/4.0 스타일: @MockBean 대신 @MockitoBean 사용
    @MockitoBean
    private PayoutFacade payoutFacade;

    @MockitoBean
    private PayoutCandidateItemRepository payoutCandidateItemRepository;

    @MockitoBean
    private PayoutRepository payoutRepository;

    private final String BASE_URL = "/api/v1/payout-batch/test";

    @Test
    @DisplayName("GET /candidates: 정산 후보 아이템 목록을 정상적으로 조회한다")
    void getCandidates_Success() throws Exception {
        when(payoutCandidateItemRepository.findAll()).thenReturn(List.of());

        mvc.perform(get(BASE_URL + "/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").exists())
                .andExpect(jsonPath("$.candidates").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("POST /run: Step 1 & 2 통합 배치를 수동 실행한다")
    void runBatchJob_Success() throws Exception {
        when(payoutFacade.collectPayoutItemsMore(anyInt()))
                .thenReturn(RsData.success("수집 성공", 10));
        when(payoutFacade.completePayoutsMore(anyInt()))
                .thenReturn(RsData.success("완료 성공", 5));

        mvc.perform(post(BASE_URL + "/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.step1ProcessedCount").value(10))
                .andExpect(jsonPath("$.step2ProcessedCount").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /step1: 정산 항목 수집(Step 1)을 개별 실행한다")
    void runStep1_Success() throws Exception {
        int limit = 20;
        when(payoutFacade.collectPayoutItemsMore(limit))
                .thenReturn(RsData.success("성공", limit));

        mvc.perform(post(BASE_URL + "/step1").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("S-200"))
                .andExpect(jsonPath("$.processedCount").value(limit))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /stats: 전체 정산 통계 정보를 확인한다")
    void getStats_Success() throws Exception {
        when(payoutCandidateItemRepository.count()).thenReturn(50L);
        when(payoutRepository.count()).thenReturn(5L);
        when(payoutCandidateItemRepository.findAll()).thenReturn(List.of());
        when(payoutRepository.findAll()).thenReturn(List.of());

        mvc.perform(get(BASE_URL + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidates.total").value(50))
                .andExpect(jsonPath("$.payouts.total").value(5))
                .andDo(print());
    }
}