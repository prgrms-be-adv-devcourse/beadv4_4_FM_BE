package backend.mossy.boundedContext.market.out;


import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.domain.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop10ByStatusOrderByCreatedAtDesc(ProductStatus status);

    Optional<Product> findById(Long productId);

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(Long id);

    @Query("select p from Product p " +
            "join fetch p.category " +
            "join fetch p.seller " +
            "left join fetch p.images " +
            "where p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

}
