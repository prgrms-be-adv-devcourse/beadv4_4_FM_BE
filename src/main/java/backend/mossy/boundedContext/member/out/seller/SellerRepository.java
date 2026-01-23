package backend.mossy.boundedContext.member.out.seller;

import backend.mossy.boundedContext.member.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByUserId(Long userId);
    boolean existsByBusinessNum(String businessNum);

}
