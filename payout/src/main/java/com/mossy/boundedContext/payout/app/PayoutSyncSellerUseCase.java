package com.mossy.boundedContext.payout.app;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.domain.PayoutSeller;
import com.mossy.boundedContext.payout.out.PayoutSellerRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase] 판매자 정보를 Payout 컨텍스트와 동기화하는 서비스 클래스
 * PayoutFacade의 '0단계: 회원/판매자 정보 동기화' 흐름에서 호출
 * Member 컨텍스트의 판매자 정보를 Payout 컨텍스트의 PayoutSeller 엔티티로 복사/업데이트하여 데이터 정합성을 유지
 */
@Service
@RequiredArgsConstructor
public class PayoutSyncSellerUseCase {
    private final PayoutSellerRepository payoutSellerRepository;
    private final EventPublisher eventPublisher;

    /**
     * Member 컨텍스트로부터 받은 SellerDto를 사용하여 PayoutSeller 엔티티를 생성하거나 업데이트
     * 기존 엔티티가 있으면 changeSeller로 업데이트하고, 없으면 새로 생성
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param seller Member 컨텍스트에서 전달된 판매자 정보 DTO
     */
    @Transactional
    public void syncSeller(SellerPayload seller) {
        if (seller == null || seller.sellerId() == null) {
            throw new DomainException(ErrorCode.INVALID_SELLER_DATA);
        }

        payoutSellerRepository.findById(seller.sellerId())
                .ifPresentOrElse(
                        // 기존 판매자: changeSeller로 업데이트 (더티 체킹으로 변경된 필드만 UPDATE)
                        existingSeller -> existingSeller.changeSeller(seller),
                        // 새 판매자: 엔티티 생성 및 이벤트 발행
                        () -> {
                            PayoutSeller newSeller = PayoutSeller.builder()
                                    .id(seller.sellerId())
                                    .userId(seller.userId())
                                    .sellerType(seller.sellerType())
                                    .storeName(seller.storeName())
                                    .businessNum(seller.businessNum())
                                    .latitude(seller.latitude())
                                    .longitude(seller.longitude())
                                    .status(seller.status())
                                    .createdAt(seller.createdAt())
                                    .updatedAt(seller.updatedAt())
                                    .build();

                            PayoutSeller saved = payoutSellerRepository.save(newSeller);
                            eventPublisher.publish(new PayoutSellerCreatedEvent(saved.toDto()));
                        }
                );
    }
}

