package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.out.external.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.repository.seller.SellerRepository;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.domain.role.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserInfoUseCase {

    private final SellerRequestRepository sellerRequestRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public UserInfoDto infoExecute(Long userId, String nickname, String name) {
        SellerRequestStatus status = sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(SellerRequest::getStatus)
                .orElse(null);

        return UserInfoDto.of(userId, nickname, name, status);
    }

    @Transactional(readOnly = true)
    public MemberAuthInfoResponse tokenExecute(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .toList();

        Long sellerId = sellerRepository.findByUserId(userId)
                .map(Seller::getId)
                .orElse(null);

        return new MemberAuthInfoResponse(
                user.getId(),
                roles,
                sellerId,
                true
        );
    }

}