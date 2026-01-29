package backend.mossy.boundedContext.market.out.order;

import backend.mossy.boundedContext.market.domain.order.DeliveryDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliveryDistanceRepository extends JpaRepository<DeliveryDistance, Long> {

    @Query("SELECT d FROM DeliveryDistance d ORDER BY d.distance ASC")
    List<DeliveryDistance> findAllByOrderByDistanceAsc();
}