package backend.mossy.boundedContext.member.app.seller;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.seller.SellerRequestRepository;
import backend.mossy.boundedContext.member.out.user.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.dto.request.SellerRequestCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerRequestUserFacade {

    private final UserRepository userRepository;
    private final SellerRequestRepository sellerRequestRepository;

    @Transactional
    public Long request(Long userId, SellerRequestCreateRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        if (sellerRequestRepository.existsByActiveUserId(userId)) {
            throw new DomainException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        String normalizedBiz = normalizeBusinessNum(req.businessNum());

        if (sellerRequestRepository.existsByBusinessNum(normalizedBiz)) {
            throw new DomainException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        SellerRequest entity = SellerRequest.pending(
                user,
                req.sellerType(),
                req.storeName(),
                normalizedBiz,
                req.representativeName(),
                req.contactEmail(),
                req.contactPhone(),
                req.address1(),
                req.address2(),
                req.latitude(),
                req.longitude()
        );

        SellerRequest saved = sellerRequestRepository.save(entity);
        return saved.getId();
    }

    private String normalizeBusinessNum(String businessNum) {
        if (businessNum == null) return null;
        return businessNum.replaceAll("\\D", ""); // 숫자만 남김
    }
}
