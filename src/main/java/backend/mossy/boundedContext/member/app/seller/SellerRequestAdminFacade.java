package backend.mossy.boundedContext.member.app.seller;

import backend.mossy.boundedContext.member.domain.Seller;
import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.seller.SellerRepository;
import backend.mossy.boundedContext.member.out.user.RoleRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.domain.seller.SellerRequestStatus;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
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
    public Long approve(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new IllegalArgumentException("판매자 신청이 '대기중'이 아닙니다.");
        }

        User user = req.getUser();
        Long userId = user.getId();

        if (sellerRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 판매자 등록이 되었습니다.");
        }

        if (sellerRepository.existsByBusinessNum(req.getBusinessNum())) {
            throw new IllegalArgumentException("이미 등록된 사업자번호입니다,");
        }

        req.approve();

        Seller seller = sellerRepository.save(Seller.createFromRequest(req));

        Role sellerRole = roleRepository.findByCode(RoleCode.SELLER)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));

        boolean hasSellerRole = user.getUserRoles().stream()
                .anyMatch(r -> r.getRole() != null && r.getRole().getCode() == RoleCode.SELLER);

        if (!hasSellerRole) {
            user.addUserRole(new UserRole(user, sellerRole));
        }

        eventPublisher.publish(new SellerApprovedEvent(
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
        ));

        return seller.getId();
    }

    @Transactional
    public void reject(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new IllegalArgumentException("판매자 신청 '대기중'이 아닙니다.");
        }

        req.reject();
    }
}
