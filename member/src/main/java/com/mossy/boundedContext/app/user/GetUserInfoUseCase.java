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
import com.mossy.boundedContext.app.mapper.UserMapper;
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
    private final UserMapper mapper;
    private final UserInfoDecryptor userInfoDecryptor;

    @Transactional(readOnly = true)
    public UserInfoDto infoExecute(Long userId) {
        SellerRequestStatus status = sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(SellerRequest::getStatus)
                .orElse(null);

        // socialAccounts를 함께 fetch해야 provider 목록을 가져올 수 있음
        User user = userRepository.findByIdWithSocialAccounts(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        // 트랜잭션 내에서 mapper 호출 (lazy loading 방지)
        // 민감정보 복호화
        return userInfoDecryptor.decryptUserInfo(user, status);
    }

    @Transactional(readOnly = true)
    public MemberAuthInfoResponse tokenExecute(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        // PENDING 유저(추가정보 미입력): roles=빈 리스트, active=false 반환 → Gateway에서 차단
        if (user.isPending()) {
            return mapper.toMemberAuthInfoResponse(user, List.of(), null);
        }

        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .toList();

        Long sellerId = sellerRepository.findByUserId(userId)
                .map(Seller::getId)
                .orElse(null);

        return mapper.toMemberAuthInfoResponse(user, roles, sellerId);
    }

}