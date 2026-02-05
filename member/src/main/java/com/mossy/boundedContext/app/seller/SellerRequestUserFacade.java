package com.mossy.boundedContext.app.seller;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.in.dto.request.SellerRequestCreateRequest;
import com.mossy.boundedContext.out.seller.SellerRequestRepository;
import com.mossy.boundedContext.out.user.UserRepository;
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
