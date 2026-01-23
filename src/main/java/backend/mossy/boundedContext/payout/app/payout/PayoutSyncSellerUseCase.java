package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.payout.event.PayoutSellerCreatedEvent;
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
     * 이를 통해 Payout 컨텍스트는 정산에 필요한 판매자 정보를 자체적으로 갖게 됨
     *
     * @param seller Member 컨텍스트에서 전달된 판매자 정보 DTO
     */
    @Transactional
    public void syncSeller(SellerApprovedEvent seller) {
        if (seller == null || seller.id() == null) {
            throw new DomainException(ErrorCode.INVALID_SELLER_DATA);
        }
        // PayoutSeller가 새로 생성되는 경우인지 확인
        boolean isNew = !payoutSellerRepository.existsById(seller.id());

        // SellerDto의 정보로 PayoutSeller 엔티티를 생성하거나 업데이트(save)
        PayoutSeller _seller = payoutSellerRepository.save(
                PayoutSeller.builder()
                        .id(seller.id())
                        .createdAt(seller.createdAt())
                        .updatedAt(seller.updatedAt())
                        .userId(seller.userId())
                        .sellerType(seller.sellerType())
                        .storeName(seller.storeName())
                        .businessNum(seller.businessNum())
                        .status(seller.status())
                        .build()
        );

        // 만약 새로운 판매자인 경우, PayoutSellerCreatedEvent를 발행하여
        // 다른 모듈(예: Cash)이 후속 작업을 수행할 수 있도록 한다
        if (isNew) {
            eventPublisher.publish(
                    new PayoutSellerCreatedEvent(
                            _seller.toDto()
                    )
            );
        }
    }
}

