package backend.mossy.boundedContext.auth.infra.security;

import backend.mossy.boundedContext.member.domain.Seller;
import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Long sellerId;

    public UserDetailsImpl(User user) {
        this(user, null);
    }

    public UserDetailsImpl(User user, Long sellerId) {
        this.user = user;
        this.sellerId = sellerId;
    }

    public Long getUserId() { return user.getId();}

    public Long getSellerId() { return sellerId; }

    public String getEmail() {
        return user.getEmail();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(role -> "ROLE_" + role.getCode().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

}
