package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.payout.dto.event.SellerDto;

public class PayoutSyncSellerUseCase {
    private PayoutSellerRepository payoutSellerRepository;
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

