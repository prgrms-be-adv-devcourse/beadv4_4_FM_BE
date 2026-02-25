package com.mossy.boundedContext.app.seller;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.in.dto.request.SellerRequestCreateRequest;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.shared.member.domain.enums.SellerType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.mossy.boundedContext.out.s3.S3Adapter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SellerRequestUserFacade {

    private final SellerRequestRepository sellerRequestRepository;
    private final S3Adapter s3Adapter;

    @Transactional
    public Long request(Long userId, SellerRequestCreateRequest req, MultipartFile profileImage) {

        // 판매자 신청 중복 체크
        if (sellerRequestRepository.existsByUserId(userId)) {
            throw new DomainException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        // BUSINESS(법인사업자)는 사업자번호 필수
        if (req.sellerType() == SellerType.BUSINESS
                && (req.businessNum() == null || req.businessNum().isBlank())) {
            throw new DomainException(ErrorCode.MISSING_BUSINESS_NUMBER);
        }

        String normalizedBiz = normalizeBusinessNum(req.businessNum());

        // businessNum이 있는 경우에만 중복 체크
        if (normalizedBiz != null && sellerRequestRepository.existsByBusinessNum(normalizedBiz)) {
            throw new DomainException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        String profileImageUrl = req.profileImageUrl();
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Adapter.uploadProfileImage(profileImage);
        } else if (profileImageUrl == null || profileImageUrl.isBlank()) {
            profileImageUrl = "default-seller";
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
                req.longitude(),
                profileImageUrl
        );

        SellerRequest saved = sellerRequestRepository.save(entity);
        return saved.getId();
    }

    private String normalizeBusinessNum(String businessNum) {
        if (businessNum == null) return null;
        return businessNum.replaceAll("\\D", ""); // 숫자만 남김
    }
}
