package backend.mossy.boundedContext.member.app.seller;

import backend.mossy.boundedContext.member.domain.Seller;
import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.seller.SellerRepository;
import backend.mossy.boundedContext.member.out.user.RoleRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.domain.seller.SellerRequestStatus;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SellerRequestAdminFacade {

    private final LockUseCase  lockUseCase;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public Long approve(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        User user = req.getUser();
        Long userId = user.getId();

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
                new SellerApprovedEvent(
                        seller.getId(),
                        seller.getUserId(),
                        seller.getSellerType(),
                        seller.getStoreName(),
                        seller.getBusinessNum(),
                        seller.getLatitude(),
                        seller.getLongitude(),
                        seller.getStatus(),
                        seller.getCreatedAt(),
                        seller.getUpdatedAt()
                )
        ));

        return seller.getId();
    }

    @Transactional
    public void reject(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        req.reject();
    }
}
