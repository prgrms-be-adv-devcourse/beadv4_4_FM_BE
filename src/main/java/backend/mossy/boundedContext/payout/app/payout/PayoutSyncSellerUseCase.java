package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.dto.event.SellerDto;
import backend.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutSyncSellerUseCase {
    private final PayoutSellerRepository payoutSellerRepository;
    private final EventPublisher eventPublisher;

    public PayoutSeller syncSeller(SellerDto seller) {
        boolean isNew = !payoutSellerRepository.existsById(seller.id());
        PayoutSeller _seller = payoutSellerRepository.save(
                PayoutSeller.builder()
                        .id(seller.id())
                        .createdAt(seller.createdAt())
                        .updatedAt(seller.updatedAt())
                        .userId(seller.userId())
                        .sellerType(seller.sellerType())
                        .storeName(seller.storeName())
                        .businessNum(seller.businessNum())
                        .representativeName(seller.representativeName())
                        .contactEmail(seller.contactEmail())
                        .contactPhone(seller.contactPhone())
                        .address1(seller.address1())
                        .address2(seller.address2())
                        .status(seller.status())
                        .build()
        );

        if (isNew) {
            eventPublisher.publish(
                    new PayoutSellerCreatedEvent(
                            _seller.toDto()
                    )
            );
        }
        return _seller;
    }
}

