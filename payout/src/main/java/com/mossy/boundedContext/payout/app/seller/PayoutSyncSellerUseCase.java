package com.mossy.boundedContext.payout.app.seller;

import com.mossy.boundedContext.payout.app.common.PayoutMapper;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.in.dto.event.PayoutSellerDto;
import com.mossy.boundedContext.payout.out.repository.PayoutSellerRepository;
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
    private final PayoutMapper payoutMapper;

    /**
     * Member 컨텍스트로부터 받은 SellerPayload를 사용하여 PayoutSeller 엔티티를 생성하거나 업데이트
     * 내부에서 PayoutSellerDto로 변환하여 처리
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

        // SellerPayload를 PayoutSellerDto로 변환
        PayoutSellerDto dto = PayoutSellerDto.from(seller);

        payoutSellerRepository.findById(dto.sellerId())
                .ifPresentOrElse(
                        // 기존 판매자: changeSeller로 업데이트 (더티 체킹으로 변경된 필드만 UPDATE)
                        existingSeller -> existingSeller.changeSeller(seller),
                        // 새 판매자: DTO로 엔티티 생성
                        () -> createPayoutSeller(dto)
                );
    }

    /**
     * PayoutSeller 엔티티를 생성하고 저장하는 헬퍼 메서드
     * @param dto 판매자 생성 DTO
     */
    private void createPayoutSeller(PayoutSellerDto dto) {
        PayoutSeller newSeller = payoutMapper.toEntity(dto);
        PayoutSeller saved = payoutSellerRepository.save(newSeller);
        eventPublisher.publish(new PayoutSellerCreatedEvent(saved.toDto()));
    }
}

