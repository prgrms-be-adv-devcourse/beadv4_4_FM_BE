package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [테스트 전용] 정산 배치 테스트 컨트롤러
 * dev 환경에서만 활성화됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/test/payout-batch")
@RequiredArgsConstructor
@Profile("dev")
@Tag(
        name = "Payout Batch Test",
        description = "정산 배치 Job 테스트용 API (dev 환경 전용)"
)
public class PayoutBatchTestController {

    private final PayoutFacade payoutFacade;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutRepository payoutRepository;

    /**
     * 1️⃣ 정산 후보 아이템 목록 조회
     */
    @Transactional(readOnly = true)
    @GetMapping("/candidates")
    @Operation(
            summary = "정산 후보 아이템 목록 조회",
            description = "정산 배치 대상이 되는 후보 아이템 목록을 조회합니다."
    )
    public Map<String, Object> getCandidates() {
        List<PayoutCandidateItem> candidates = payoutCandidateItemRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", candidates.size());
        response.put("candidates", candidates.stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("eventType", item.getEventType());
            itemMap.put("amount", item.getAmount());
            itemMap.put("paymentDate", item.getPaymentDate());
            itemMap.put("payeeName", item.getPayee().getStoreName());
            itemMap.put("isProcessed", item.getPayoutItem() != null);
            itemMap.put("createdAt", item.getCreatedAt());
            return itemMap;
        }).toList());

        log.info("정산 후보 아이템 조회: {}건", candidates.size());
        return response;
    }

    /**
     * 2️⃣ 배치 Job 전체 수동 실행
     */
    @Transactional
    @PostMapping("/run")
    @Operation(
            summary = "정산 배치 Job 수동 실행",
            description = "정산 배치 Job을 수동으로 실행합니다. (Step1 + Step2)"
    )
    public Map<String, Object> runBatchJob() {
        Map<String, Object> response = new HashMap<>();

        try {
            LocalDateTime startTime = LocalDateTime.now();

            RsData<Integer> step1Result = payoutFacade.collectPayoutItemsMore(100);
            RsData<Integer> step2Result = payoutFacade.completePayoutsMore(100);

            LocalDateTime endTime = LocalDateTime.now();

            response.put("status", "COMPLETED");
            response.put("step1ProcessedCount", step1Result.getData());
            response.put("step1Message", step1Result.getMsg());
            response.put("step2ProcessedCount", step2Result.getData());
            response.put("step2Message", step2Result.getMsg());
            response.put("startTime", startTime);
            response.put("endTime", endTime);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * 3️⃣ 정산 결과(Payout) 목록 조회
     */
    @Transactional(readOnly = true)
    @GetMapping("/payouts")
    @Operation(
            summary = "정산 결과 목록 조회",
            description = "배치 실행 후 생성된 정산(Payout) 목록을 조회합니다."
    )
    public Map<String, Object> getPayouts() {
        List<Payout> payouts = payoutRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", payouts.size());
        response.put("payouts", payouts.stream().map(payout -> {
            Map<String, Object> payoutMap = new HashMap<>();
            payoutMap.put("id", payout.getId());
            payoutMap.put("payeeName", payout.getPayee().getStoreName());
            payoutMap.put("amount", payout.getAmount());
            payoutMap.put("itemCount", payout.getItems().size());
            payoutMap.put("isCompleted", payout.getPayoutDate() != null);
            payoutMap.put("payoutDate", payout.getPayoutDate());
            payoutMap.put("createdAt", payout.getCreatedAt());
            return payoutMap;
        }).toList());

        return response;
    }

    /**
     * 4️⃣ Step 1 단독 실행
     */
    @Transactional
    @PostMapping("/step1")
    @Operation(
            summary = "정산 Step1 실행",
            description = "정산 후보 아이템을 정산 아이템으로 수집합니다."
    )
    public Map<String, Object> runStep1(
            @RequestParam(defaultValue = "100") int limit
    ) {
        RsData<Integer> result = payoutFacade.collectPayoutItemsMore(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", result.getResultCode());
        response.put("message", result.getMsg());
        response.put("processedCount", result.getData());

        return response;
    }

    /**
     * 5️⃣ Step 2 단독 실행
     */
    @Transactional
    @PostMapping("/step2")
    @Operation(
            summary = "정산 Step2 실행",
            description = "정산 아이템을 정산 완료 처리합니다."
    )
    public Map<String, Object> runStep2(
            @RequestParam(defaultValue = "100") int limit
    ) {
        RsData<Integer> result = payoutFacade.completePayoutsMore(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", result.getResultCode());
        response.put("message", result.getMsg());
        response.put("processedCount", result.getData());

        return response;
    }

    /**
     * 6️⃣ 정산 배치 통계 조회
     */
    @Transactional(readOnly = true)
    @GetMapping("/stats")
    @Operation(
            summary = "정산 배치 통계 조회",
            description = "정산 후보 및 정산 결과에 대한 전체 통계를 조회합니다."
    )
    public Map<String, Object> getStats() {
        long totalCandidates = payoutCandidateItemRepository.count();
        long processedCandidates = payoutCandidateItemRepository.findAll().stream()
                .filter(item -> item.getPayoutItem() != null)
                .count();

        long totalPayouts = payoutRepository.count();
        long completedPayouts = payoutRepository.findAll().stream()
                .filter(payout -> payout.getPayoutDate() != null)
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("candidates", Map.of(
                "total", totalCandidates,
                "processed", processedCandidates,
                "unprocessed", totalCandidates - processedCandidates
        ));
        response.put("payouts", Map.of(
                "total", totalPayouts,
                "completed", completedPayouts,
                "active", totalPayouts - completedPayouts
        ));

        return response;
    }
}