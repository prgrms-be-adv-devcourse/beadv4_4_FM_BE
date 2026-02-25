package com.mossy.boundedContext.app.seller;


import com.mossy.boundedContext.app.mapper.SellerMapper;
import com.mossy.boundedContext.app.mapper.SellerRequestMapper;
import com.mossy.boundedContext.domain.role.UserRole;
import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.response.SellerRequestListDto;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.out.repository.seller.SellerRepository;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.boundedContext.out.repository.user.RoleRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerRequestAdminFacade {

    private final LockUseCase lockUseCase;
    private final SellerRepository sellerRepository;
    private final SellerRequestRepository sellerRequestRepository;
    private final SellerMapper sellerMapper;
    private final SellerRequestMapper sellerRequestMapper;
    private final RoleRepository roleRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
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

        // businessNum이 있는 경우에만 중복 체크 (개인사업자는 null 가능)
        if (req.getBusinessNum() != null && sellerRepository.existsByBusinessNum(req.getBusinessNum())) {
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

        // SellerJoinedEvent 발행 → SellerKafkaEventPublisher가 AFTER_COMMIT 시 Kafka로 전달
        kafkaEventPublisher.publish(sellerMapper.toSellerJoinedEvent(seller));

        return new SellerAppoveResult(seller.getId(), userId);
    }

    // 외부 서비스 호출 실패 시 보상 트랜잭션 (롤백)
    @Transactional
    public void rollbackApprove(Long sellerId, Long userId) {
        // Seller 삭제
        sellerRepository.deleteById(sellerId);

        // User의 SELLER 역할 제거
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        user.getUserRoles().removeIf(ur -> ur.getRole().getCode() == RoleCode.SELLER);

        // SellerRequest 상태를 다시 PENDING으로 복원
        SellerRequest req = sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_REQUEST_NOT_FOUND));

        req.restoreToPending();
    }

    @Transactional
    public void reject(Long requestId) {
        SellerRequest req = lockUseCase.lockAndGet(requestId);

        if (req.getStatus() != SellerRequestStatus.PENDING) {
            throw new DomainException(ErrorCode.SELLER_REQUEST_NOT_PENDING);
        }

        req.reject();
    }

    @Transactional(readOnly = true)
    public List<SellerRequestListDto> getPendingRequests() {
        List<SellerRequest> sellerRequests = sellerRequestRepository.findByStatus(SellerRequestStatus.PENDING);
        return sellerRequestMapper.toSellerRequestListDtos(sellerRequests);
    }

    public record SellerAppoveResult(Long sellerId, Long userId) {}
}
