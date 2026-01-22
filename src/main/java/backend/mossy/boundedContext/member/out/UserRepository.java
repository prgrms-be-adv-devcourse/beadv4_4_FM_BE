package backend.mossy.boundedContext.member.out;

import backend.mossy.boundedContext.member.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
