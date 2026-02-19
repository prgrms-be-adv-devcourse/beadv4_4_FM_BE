package com.mossy.boundedContext.global.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final Long sellerId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    // OAuth2 로그인 및 JWT 인증용 생성자 (최소 정보)
    public UserDetailsImpl(Long userId, String role, Long sellerId) {
        this.userId = userId;
        this.email = null;
        this.password = null;
        this.role = role != null ? role : "USER";
        this.sellerId = sellerId;
        this.enabled = true;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
        );
    }

    // 기존 로그인 생성자 (일반 로그인용 - 이메일/비밀번호)
    // 하위 호환성 유지를 위해 남겨둠
    @SuppressWarnings("unused")
    public UserDetailsImpl(
            Long userId,
            String email,
            String password,
            List<String> roles,
            Long sellerId,
            boolean enabled
    ) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = (roles != null && !roles.isEmpty()) ? roles.getFirst() : "USER";
        this.sellerId = sellerId;
        this.enabled = enabled;
        this.authorities = (roles != null)
                ? roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList()
                : Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
