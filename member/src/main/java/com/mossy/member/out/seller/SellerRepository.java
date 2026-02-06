package com.mossy.member.out.seller;

import com.mossy.member.domain.seller.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByUserId(Long userId);
    boolean existsByBusinessNum(String businessNum);

    Optional<Seller> findByUserId(Long userId);

}
