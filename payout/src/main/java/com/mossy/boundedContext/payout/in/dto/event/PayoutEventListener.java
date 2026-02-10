package com.mossy.boundedContext.payout.in.dto.event;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.donation.app.DonationFacade;
import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.in.dto.command.CreatePayoutCandidateDto;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.shared.market.event.OrderPaidEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.payout.enums.PayoutEventType;
import com.mossy.shared.payout.event.PayoutCompletedEvent;
import com.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import com.mossy.boundedContext.payout.app.PayoutSupport;
import com.mossy.boundedContext.payout.app.common.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PayoutEventListener {
    private final PayoutFacade payoutFacade;
    private final DonationFacade donationFacade;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutSupport payoutSupport;
    private final DistanceCalculator distanceCalculator;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerJoinedEvent(SellerJoinedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userJoinedEvent(UserJoinedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userUpdatedEvent(UserUpdatedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutSellerCreatedEvent(PayoutSellerCreatedEvent event) {
        payoutFacade.createPayout(event.seller().sellerId());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void orderPaidEvent(OrderPaidEvent event) {
        // Market 도메인에서 주문 결제 완료 시 발행되는 이벤트 처리
        // 이벤트에 포함된 OrderItem들을 사용하여 정산 후보 생성
        event.orderItems()
                .forEach(orderItem -> {
                    // 1. buyer와 seller 조회
                    var buyer = payoutSupport.findUserById(event.buyerId())
                            .orElseThrow(() -> new DomainException(ErrorCode.BUYER_NOT_FOUND));
                    var seller = payoutSupport.findSellerById(orderItem.sellerId())
                            .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

                    // 2. 배송 거리 계산 (buyer와 seller의 위도/경도 기반)
                    var deliveryDistance = distanceCalculator.calculateDistance(
                            buyer.getLatitude(), buyer.getLongitude(),
                            seller.getLatitude(), seller.getLongitude()
                    );

                    // 3. Payout 도메인 내부용 DTO 생성
                    var createPayoutCandidateDto = CreatePayoutCandidateDto.builder()
                            .orderItemId(orderItem.orderItemId())
                            .orderId(event.orderId())
                            .buyerId(event.buyerId())
                            .buyerName(event.buyerName())
                            .sellerId(orderItem.sellerId())
                            .productId(orderItem.productId())
                            .orderPrice(orderItem.orderPrice())
                            .orderItemCreatedAt(orderItem.createdAt())
                            .orderItemUpdatedAt(orderItem.updatedAt())
                            .weightGrade("소형")  // 최저 단계로 임시 설정
                            .deliveryDistance(deliveryDistance)  // 계산된 거리
                            .paymentDate(event.createdAt())  // 결제 일시
                            .build();

                    // 4. 정산 후보 항목 생성 (수수료, 판매 대금, 기부금)
                    payoutFacade.addPayoutCandidateItem(createPayoutCandidateDto);
                });
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) {
        Long payoutId = event.payout().id();

        // 1. 정산 완료된 Payout에 포함된 기부금 PayoutCandidateItem들을 조회
        List<PayoutCandidateItem> donationCandidates = payoutCandidateItemRepository
                .findByPayoutItem_Payout_IdAndEventType(payoutId, PayoutEventType.정산__상품판매_기부금);

        // 2. 각 기부금 후보 항목에 대해 기부 로그 생성
        donationCandidates.forEach(candidate -> {
            donationFacade.createDonationLogDirect(
                    candidate.getRelId(),                    // orderItemId
                    candidate.getPayer().getId(),        // buyerId
                    candidate.getAmount(),                   // 이미 계산된 기부금액
                    candidate.getWeightGrade(),              // weightGrade
                    candidate.getDeliveryDistance()          // deliveryDistance
            );
        });

        // 3. 정산 완료된 기부 로그 업데이트
        donationFacade.settleDonationLogs(payoutId);

        // 4. 다음 정산을 위한 새 Payout 생성
        payoutFacade.createPayout(event.payout().payeeId());
    }
}