package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * [UseCase] 생성된 정산(Payout)을 실제 완료 처리하는 서비스 클래스
 * PayoutFacade의 '3단계: 정산 실행 및 완료' 흐름에 해당하며, 배치(Batch) 작업으로 실행되는 것을 가정
 * 이 프로세스가 완료되면 PayoutCompletedEvent가 발행되어, 기부금 정산 등의 후속 작업이 트리거
 */
@Service
@RequiredArgsConstructor
public class PayoutCompletePayoutsMoreUseCase {
    private final PayoutRepository payoutRepository;

    /**
     * 아직 완료되지 않은 Payout들을 조회하여, '정산 완료' 상태로 처리
     * Payout의 completePayout() 메서드 호출을 통해 상태 변경 및 PayoutCompletedEvent 발행이 이루어짐
     * Spring Batch에서 트랜잭션을 관리하므로 별도 트랜잭션 어노테이션 제거
     *
     * @param limit 한 번의 배치 작업에서 처리할 최대 Payout 수
     * @return 처리 결과 RsData
     */
    public RsData<Integer> completePayoutsMore(int limit) {
        // 1. 현재 활성화되어 있는 (아직 정산일이 지정되지 않은) Payout들을 조회
        List<Payout> activePayouts = findActivePayouts(limit);

        // 2. 처리할 Payout이 없으면 작업을 종료합니다.
        if (activePayouts.isEmpty())
            return new RsData<>("200-1", "더 이상 정산할 정산내역이 없습니다.", 0);

        // 3. 조회된 모든 Payout에 대해 completePayout 메서드를 호출하여 완료 처리
        //    이 메서드 내부에서 PayoutCompletedEvent가 발행
        activePayouts.forEach(Payout::completePayout);

        return new RsData<>(
                "201-1",
                "%d건의 정산이 처리되었습니다.".formatted(activePayouts.size()),
                activePayouts.size()
        );
    }

    /**
     * 아직 정산일이 지정되지 않았고(PayoutDate == null) 금액이 0보다 큰 Payout들을 조회
     * 이는 정산이 필요한 활성화된 Payout을 의미
     *
     * @param limit 조회할 최대 Payout 수
     * @return 활성화된 Payout 리스트
     */
    private List<Payout> findActivePayouts(int limit) {
        return payoutRepository.findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(BigDecimal.ZERO, PageRequest.of(0, limit));
    }
}