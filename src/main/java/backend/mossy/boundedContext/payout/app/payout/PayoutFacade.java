package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.shared.payout.dto.response.payout.PayoutCandidateItemResponse;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import backend.mossy.shared.member.dto.event.SellerDto;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 정산(Payout) 기능의 메인 진입점 역할을 하는 파사드(Facade)
 * 정산과 관련된 복잡한 단계별 비즈니스 로직(Use Case)들을 외부에 제공
 * <p>
 * 정산 플로우 (자동화된 배치 프로세스 기준):
 * 0. [사전준비] 회원/판매자 정보 동기화: Member 컨텍스트의 변경사항을 Payout 컨텍스트에 동기화 (syncSeller, syncUser)
 * 1. [실시간] 정산 후보 아이템 생성: 주문이 완료되면, 해당 주문건에 대한 정산 후보 아이템(PayoutCandidateItem)을 생성 (addPayoutCandidateItems)
 * 2. [배치] 정산 아이템 집계: 스케줄러를 통해 주기적으로 정산 후보 아이템들을 실제 정산(Payout)에 포함될 아이템으로 집계/변환 (collectPayoutItemsMore)
 * 3. [배치] 정산 실행 및 완료: 스케줄러를 통해 주기적으로 생성된 정산(Payout)들을 실제 실행하고 완료 처리,  이 과정에서 PayoutCompletedEvent가 발행 (completePayoutsMore)
 */
@Service
@RequiredArgsConstructor
public class PayoutFacade {
    private final PayoutSyncSellerUseCase payoutSyncSellerUseCase;
    private final PayoutSyncUserUseCase payoutSyncUserUseCase;
    private final PayoutCreatePayoutUseCase payoutCreatePayoutUseCase;
    private final PayoutAddPayoutCandidateItemsUseCase payoutAddPayoutCandidateItemsUseCase;
    private final PayoutCollectPayoutItemsMoreUseCase payoutCollectPayoutItemsMoreUseCase;
    private final PayoutCompletePayoutsMoreUseCase payoutCompletePayoutsMoreUseCase;
    private final PayoutSupport payoutSupport;

    /**
     * [흐름 0] 판매자 정보를 Payout 컨텍스트와 동기화
     * Member 컨텍스트에서 판매자 정보가 변경될 때 호출
     * @param seller 변경된 판매자 정보 DTO
     */
    @Transactional
    public void syncSeller(SellerDto seller) {
        payoutSyncSellerUseCase.syncSeller(seller);
    }

    /**
     * [흐름 0] 사용자 정보를 Payout 컨텍스트와 동기화
     * Member 컨텍스트에서 사용자 정보가 변경될 때 호출
     * @param user 변경된 사용자 정보 DTO
     */
    @Transactional
    public void syncUser(UserDto user) {
        payoutSyncUserUseCase.syncUser(user);
    }

    /**
     * [수동] 특정 수취인(payee)에 대한 정산을 수동으로 생성
     * 자동화된 배치 프로세스와는 별개의 흐름
     * @param payeeId 수취인의 ID
     * @return 생성된 Payout 객체
     */
    @Transactional
    public Payout createPayout(Long payeeId) {
        return payoutCreatePayoutUseCase.createPayout(payeeId);
    }

    /**
     * [흐름 1] 단일 주문 아이템을 바탕으로 정산 후보 아이템(PayoutCandidateItem)을 추가
     * Payment 컨텍스트에서 결제가 완료될 때 호출
     * @param orderItem 주문 아이템 DTO
     * @param paymentDate 결제 완료 일시
     */
    @Transactional
    public void addPayoutCandidateItem(OrderItemDto orderItem, LocalDateTime paymentDate) {
        payoutAddPayoutCandidateItemsUseCase.addPayoutCandidateItem(orderItem, paymentDate);
    }

    /**
     * [흐름 2] 정산 후보 아이템을 집계하여 실제 정산(Payout)에 포함될 PayoutItem으로 변환하는 배치를 실행
     * Spring Batch의 트랜잭션을 사용하므로 MANDATORY 전파 레벨 사용
     * @param limit 한 번에 처리할 개수
     * @return 처리 결과 (성공/실패, 처리된 개수 등)
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    public RsData<Integer> collectPayoutItemsMore(int limit) {
        return payoutCollectPayoutItemsMoreUseCase.collectPayoutItemsMore(limit);
    }

    /**
     * 현재 생성되어 있는 정산 후보 아이템 목록을 조회합니다. (읽기 전용)
     * @return 정산 후보 아이템 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<PayoutCandidateItemResponse> findPayoutCandidateItems() {
        return payoutSupport.findPayoutCandidateItems()
                .stream()
                .map(PayoutCandidateItemResponse::from)
                .toList();
    }

    /**
     * [흐름 3] 생성된 정산(Payout)들을 실제로 실행하고 완료 처리하는 배치를 실행
     * 이 과정이 성공적으로 끝나면 PayoutCompletedEvent가 발행되어, 기부금 정산 등 후속 조치가 이어짐
     * Spring Batch의 트랜잭션을 사용하므로 MANDATORY 전파 레벨 사용
     * @param limit 한 번에 처리할 개수
     * @return 처리 결과 (성공/실패, 처리된 개수 등)
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    public RsData<Integer> completePayoutsMore(int limit) {
        return payoutCompletePayoutsMoreUseCase.completePayoutsMore(limit);
    }
}
