package com.mossy.member.app.seller;

import com.mossy.member.out.seller.SellerRequestRepository;
import com.mossy.member.out.user.UserRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.member.domain.seller.SellerRequest;
import com.mossy.shared.member.dto.request.SellerRequestCreateRequest;
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
                // userID 수정
                userId,
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
