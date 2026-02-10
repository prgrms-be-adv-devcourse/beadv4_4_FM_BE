package com.mossy.boundedContext.out.user;

import com.mossy.shared.member.domain.role.Role;
import com.mossy.shared.member.domain.role.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
}
