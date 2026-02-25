package com.mossy.boundedContext.out.repository.user;

import com.mossy.boundedContext.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    @Query("""
        select u from User u
            left join fetch u.userRoles ur
            left join fetch ur.role r
        where u.email = :email
""")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
        select u from User u
            left join fetch u.userRoles ur
            left join fetch ur.role r
        where u.id = :id
""")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("""
        select u from User u
            left join fetch u.socialAccounts sa
        where u.id = :id
""")
    Optional<User> findByIdWithSocialAccounts(@Param("id") Long id);


    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("""
        SELECT u FROM User u
        WHERE NOT EXISTS (
            SELECT ur FROM UserRole ur
            WHERE ur.user = u AND ur.role.code = com.mossy.shared.member.domain.role.RoleCode.SELLER
        ) AND u.status != com.mossy.shared.member.domain.enums.UserStatus.DELETED
    """)
    Page<User> findBuyers(Pageable pageable);

    List<User> findAllByIdIn(List<Long> ids);
}
