package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.member.domain.role.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerfyMemberUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public MemberVerifyExternResponse execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        boolean isValid = passwordEncoder.matches(password, user.getPassword());

        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .toList();

        return mapper.toMemberVerifyResponse(user, roles, isValid);
    }
}
