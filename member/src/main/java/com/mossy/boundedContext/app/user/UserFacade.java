package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.app.VerfyMemberUseCase;
import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final SignupUseCase signupUseCase;
    private final EventPublisher eventPublisher;
    private final VerfyMemberUseCase verfyMemberUseCase;
    private final UserMapper userMapper;
    private final SellerRequestRepository sellerRequestRepository;

    public Long signup(SignupRequest req){
        User savedUser = signupUseCase.execute(req);

        UserPayload userPayload = userMapper.toPayload(savedUser);
        eventPublisher.publish(new UserJoinedEvent (userPayload));

        return savedUser.getId();
    }

    public UserInfoDto getUserInfo(Long userId, String nickname, String name) {
        SellerRequestStatus status = sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(SellerRequest::getStatus)
                .orElse(null);

        return UserInfoDto.of(userId, nickname, name, status);
    }


    public MemberVerifyExternResponse verifyMember(String email, String password) {
        return verfyMemberUseCase.execute(email, password);
    }


}