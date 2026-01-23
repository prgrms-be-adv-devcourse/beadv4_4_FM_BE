package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
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
 * [테스트 전용] 정산 배치 테스트를 위한 컨트롤러
 * dev 환경에서만 활성화됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/test/payout-batch")
@RequiredArgsConstructor
@Profile("dev") // dev 환경에서만 활성화
public class PayoutBatchTestController {

    private final PayoutFacade payoutFacade;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutRepository payoutRepository;

    /**
     * 1️⃣ [테스트 시작] 정산 후보 아이템 목록 조회
     * GET /api/test/payout-batch/candidates
     */
    @Transactional(readOnly = true)
    @GetMapping("/candidates")
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

        log.info("📋 정산 후보 아이템 조회: 총 {}건", candidates.size());
        return response;
    }

    /**
     * 2️⃣ [테스트 실행] 배치 Job 수동 실행
     * POST /api/test/payout-batch/run
     */
    @Transactional
    @PostMapping("/run")
    public Map<String, Object> runBatchJob() {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("🚀 [배치 수동 실행] 시작");
            LocalDateTime startTime = LocalDateTime.now();

            // Step 1: 정산 항목 수집
            RsData<Integer> step1Result = payoutFacade.collectPayoutItemsMore(100);
            log.info("📦 [Step 1 완료] 처리된 항목: {}건", step1Result.getData());

            // Step 2: 정산 완료 처리
            RsData<Integer> step2Result = payoutFacade.completePayoutsMore(100);
            log.info("💰 [Step 2 완료] 처리된 정산: {}건", step2Result.getData());

            LocalDateTime endTime = LocalDateTime.now();

            response.put("status", "COMPLETED");
            response.put("step1ProcessedCount", step1Result.getData());
            response.put("step1Message", step1Result.getMsg());
            response.put("step2ProcessedCount", step2Result.getData());
            response.put("step2Message", step2Result.getMsg());
            response.put("startTime", startTime);
            response.put("endTime", endTime);

            log.info("✅ [배치 실행 완료] Step1: {}건, Step2: {}건",
                    step1Result.getData(), step2Result.getData());

        } catch (Exception e) {
            log.error("❌ [배치 실행 실패] 에러: {}", e.getMessage(), e);
            response.put("error", e.getMessage());
            response.put("status", "FAILED");
        }

        return response;
    }

    /**
     * 3️⃣ [결과 확인] 정산(Payout) 목록 조회
     * GET /api/test/payout-batch/payouts
     */
    @Transactional(readOnly = true)
    @GetMapping("/payouts")
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

        log.info("💰 정산(Payout) 조회: 총 {}건", payouts.size());
        return response;
    }

    /**
     * 4️⃣ [직접 테스트] Step 1 실행 (정산 후보 → 정산 아이템)
     * POST /api/test/payout-batch/step1
     */
    @Transactional
    @PostMapping("/step1")
    public Map<String, Object> runStep1(@RequestParam(defaultValue = "100") int limit) {
        log.info("🔧 [Step 1 직접 실행] 시작 - limit: {}", limit);

        RsData<Integer> result = payoutFacade.collectPayoutItemsMore(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", result.getResultCode());
        response.put("message", result.getMsg());
        response.put("processedCount", result.getData());

        log.info("✅ [Step 1 완료] 처리된 항목: {}건", result.getData());
        return response;
    }

    /**
     * 5️⃣ [직접 테스트] Step 2 실행 (정산 완료 처리)
     * POST /api/test/payout-batch/step2
     */
    @Transactional
    @PostMapping("/step2")
    public Map<String, Object> runStep2(@RequestParam(defaultValue = "100") int limit) {
        log.info("🔧 [Step 2 직접 실행] 시작 - limit: {}", limit);

        RsData<Integer> result = payoutFacade.completePayoutsMore(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", result.getResultCode());
        response.put("message", result.getMsg());
        response.put("processedCount", result.getData());

        log.info("✅ [Step 2 완료] 처리된 정산: {}건", result.getData());
        return response;
    }

    /**
     * 6️⃣ [통계 확인] 전체 통계 조회
     * GET /api/test/payout-batch/stats
     */
    @Transactional(readOnly = true)
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long totalCandidates = payoutCandidateItemRepository.count();
        long processedCandidates = payoutCandidateItemRepository.findAll().stream()
                .filter(item -> item.getPayoutItem() != null)
                .count();
        long unprocessedCandidates = totalCandidates - processedCandidates;

        long totalPayouts = payoutRepository.count();
        long completedPayouts = payoutRepository.findAll().stream()
                .filter(payout -> payout.getPayoutDate() != null)
                .count();
        long activePayouts = totalPayouts - completedPayouts;

        Map<String, Object> response = new HashMap<>();
        response.put("candidates", Map.of(
                "total", totalCandidates,
                "processed", processedCandidates,
                "unprocessed", unprocessedCandidates
        ));
        response.put("payouts", Map.of(
                "total", totalPayouts,
                "completed", completedPayouts,
                "active", activePayouts
        ));

        log.info("📊 통계 - 후보: {}/{}, 정산: {}/{}",
                unprocessedCandidates, totalCandidates,
                completedPayouts, totalPayouts);

        return response;
    }
}
