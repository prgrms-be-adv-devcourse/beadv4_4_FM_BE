package backend.mossy.boundedContext.member.app.seller;

import backend.mossy.boundedContext.member.out.seller.SellerRequestRepository;
import backend.mossy.shared.member.domain.seller.SellerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockUseCase {

    private final SellerRequestRepository sellerRequestRepository;

    public SellerRequest lockAndGet(Long requestId) {
        return sellerRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));
    }
}
