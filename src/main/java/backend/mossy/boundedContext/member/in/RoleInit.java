package backend.mossy.boundedContext.member.in;

import backend.mossy.boundedContext.member.out.user.RoleRepository;
import backend.mossy.shared.member.domain.role.Role;
import backend.mossy.shared.member.domain.role.RoleCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoleInit {

    @Bean
    @Transactional
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByCode(RoleCode.USER).isEmpty()) {
                roleRepository.save(new Role(RoleCode.USER, "일반회원"));
            }

            if (roleRepository.findByCode(RoleCode.SELLER).isEmpty()) {
                roleRepository.save(new Role(RoleCode.SELLER, "판매자"));
            }

            if (roleRepository.findByCode(RoleCode.ADMIN).isEmpty()) {
                roleRepository.save(new Role(RoleCode.ADMIN, "관리자"));
            }
        };
    }
}
