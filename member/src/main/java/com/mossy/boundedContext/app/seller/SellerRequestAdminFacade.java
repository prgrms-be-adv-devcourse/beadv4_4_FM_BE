package com.mossy.boundedContext.app.seller;


import com.mossy.boundedContext.domain.role.UserRole;
import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.out.seller.SellerRepository;
import com.mossy.boundedContext.out.user.RoleRepository;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.payload.SellerPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerRequestAdminFacade {

    private final LockUseCase  lockUseCase;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final EventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Transactional
    public SellerAppoveResult approve(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        Long userId = req.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        if (sellerRepository.existsByUserId(userId)) {
            throw new DomainException(ErrorCode.DUPLICATE_SELLER);
        }

        if (sellerRepository.existsByBusinessNum(req.getBusinessNum())) {
            throw new DomainException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        req.approve();

        Seller seller = sellerRepository.save(Seller.createFromRequest(req));

        Role sellerRole = roleRepository.findByCode(RoleCode.SELLER)
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

        boolean hasSellerRole = user.getUserRoles().stream()
                .anyMatch(r -> r.getRole() != null && r.getRole().getCode() == RoleCode.SELLER);

        if (!hasSellerRole) {
            user.addUserRole(new UserRole(user, sellerRole));
        }

        eventPublisher.publish(new SellerJoinedEvent(
                SellerPayload.builder()
                        .sellerId(seller.getId())
                        .userId(seller.getUserId())
                        .sellerType(seller.getSellerType())
                        .storeName(seller.getStoreName())
                        .businessNum(seller.getBusinessNum())
                        .latitude(seller.getLatitude())
                        .longitude(seller.getLongitude())
                        .status(seller.getStatus())
                        .createdAt(seller.getCreatedAt())
                        .updatedAt(seller.getUpdatedAt())
                        .build()

        ));

        return new SellerAppoveResult(seller.getId(), userId);
    }

    @Transactional
    public void reject(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        req.reject();
    }
    
    public record SellerAppoveResult(Long sellerId, Long userId) {}
}
