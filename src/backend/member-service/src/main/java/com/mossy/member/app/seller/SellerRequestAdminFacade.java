package com.mossy.member.app.seller;

import com.mossy.member.domain.seller.Seller;
import com.mossy.member.out.seller.SellerRepository;
import com.mossy.member.out.user.RoleRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.member.domain.seller.SellerRequest;
import com.mossy.member.domain.seller.SellerRequestStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SellerRequestAdminFacade {

    private final LockUseCase  lockUseCase;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public SellerAppoveResult approve(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        Long userId = req.getUserId();

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

//        boolean hasSellerRole = user.getUserRoles().stream()
//                .anyMatch(r -> r.getRole() != null && r.getRole().getCode() == RoleCode.SELLER);
//
//        if (!hasSellerRole) {
//            user.addUserRole(new UserRole(user, sellerRole));
//        }
//
//        eventPublisher.publish(new SellerJoinedEvent(
//                new SellerApprovedEvent(
//                        seller.getId(),
//                        seller.getUserId(),
//                        seller.getSellerType(),
//                        seller.getStoreName(),
//                        seller.getBusinessNum(),
//                        seller.getLatitude(),
//                        seller.getLongitude(),
//                        seller.getStatus(),
//                        seller.getCreatedAt(),
//                        seller.getUpdatedAt()
//                )
//        ));

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
