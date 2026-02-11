package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserInfoUseCase {

    private final SellerRequestRepository sellerRequestRepository;

    public UserInfoDto execute(Long userId, String nickname, String name) {
        SellerRequestStatus status = sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(SellerRequest::getStatus)
                .orElse(null);

        return UserInfoDto.of(userId, nickname, name, status);
    }
}