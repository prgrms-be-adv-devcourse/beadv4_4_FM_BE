package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.out.user.UserRepository;
import com.mossy.shared.auth.domain.response.MemberVerifyResponse;
import com.mossy.shared.member.domain.role.RoleCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerfyMemberUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberVerifyResponse execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        boolean isValid = passwordEncoder.matches(password, user.getPassword());

        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .collect(Collectors.toList());

        return new MemberVerifyResponse(user.getId(), roles, isValid);

    }
}
