package com.mossy.boundedContext.app;

import com.mossy.boundedContext.domain.seller.SourceSeller;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import com.mossy.boundedContext.out.repository.seller.SellerRepository;
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
    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public MemberVerifyExternResponse execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        boolean isValid = passwordEncoder.matches(password, user.getPassword());

        List<RoleCode> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .toList();

        Long sellerId = null;
        if (roles.contains(RoleCode.SELLER)) {
            sellerId = sellerRepository.findByUserId(user.getId())
                    .map(SourceSeller::getId)
                    .orElse(null);
        }
        return mapper.toMemberVerifyResponse(user, roles, isValid, sellerId);
    }
}
