package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.domain.event.PayoutSellerCreatedEvent;
import backend.mossy.boundedContext.payout.out.PayoutSellerRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.domain.seller.SourceSeller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayoutSyncSellerUseCase {
    private final PayoutSellerRepository payoutSellerRepository;
    private final EventPublisher eventPublisher;

    public PayoutSeller syncSeller(SourceSeller sourceSeller) {
        return payoutSellerRepository.findById(sourceSeller.getId())
                .orElseGet(() -> {
                    PayoutSeller payoutSeller = PayoutSeller.builder()
                            .id(sourceSeller.getId())
                            .createdAt(sourceSeller.getCreatedAt())
                            .updatedAt(sourceSeller.getUpdatedAt())
                            .userId(sourceSeller.getUserId())
                            .sellerType(sourceSeller.getSellerType())
                            .storeName(sourceSeller.getStoreName())
                            .businessNum(sourceSeller.getBusinessNum())
                            .representativeName(sourceSeller.getRepresentativeName())
                            .contactEmail(sourceSeller.getContactEmail())
                            .contactPhone(sourceSeller.getContactPhone())
                            .address1(sourceSeller.getAddress1())
                            .address2(sourceSeller.getAddress2())
                            .status(sourceSeller.getStatus())
                            .build();

                    payoutSellerRepository.save(payoutSeller);

                    eventPublisher.publish(new PayoutSellerCreatedEvent(payoutSeller.toDto()));

                    return payoutSeller;
                });
    }
}
