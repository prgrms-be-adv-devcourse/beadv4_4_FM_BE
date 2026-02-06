package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.app.VerfyMemberUseCase;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.auth.domain.response.MemberVerifyResponse;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.boundedContext.in.dto.UserDtoMapper;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;
    private final SignupUseCase signupUseCase;
    private final EventPublisher eventPublisher;
    private final VerfyMemberUseCase verfyMemberUseCase;

    public Long signup(SignupRequest req){
        User savedUser = signupUseCase.execute(req);

        UserPayload userPayload = UserDtoMapper.from(savedUser);
        eventPublisher.publish(new UserJoinedEvent (userPayload));

        return savedUser.getId();
    }

    public MemberVerifyResponse verifyMember(String email, String password) {
        return verfyMemberUseCase.execute(email, password);
    }


}