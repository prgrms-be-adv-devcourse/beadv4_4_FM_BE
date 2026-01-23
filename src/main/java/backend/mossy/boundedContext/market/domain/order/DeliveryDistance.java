package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MARKET_DELIVERY_DISTANCE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@AttributeOverride(name = "id", column = @Column(name = "delivery_distance_id"))
public class DeliveryDistance extends BaseIdAndTime {

    private static final int EARTH_RADIUS_KM = 6371;

    @Column(nullable = false)
    private String distanceName;

    @Column(nullable = false)
    private int distance;

    public static int calculateDistance(
            BigDecimal buyerLat, BigDecimal buyerLon,
            BigDecimal sellerLat, BigDecimal sellerLon
    ) {
        double latRad1 = Math.toRadians(buyerLat.doubleValue());
        double latRad2 = Math.toRadians(sellerLat.doubleValue());
        double deltaLat = Math.toRadians(sellerLat.subtract(buyerLat).doubleValue());
        double deltaLon = Math.toRadians(sellerLon.subtract(buyerLon).doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) Math.round(EARTH_RADIUS_KM * c);
    }

    public static DeliveryDistance findByDistance(List<DeliveryDistance> grades, int distance) {
        for (DeliveryDistance grade : grades) {
            if (distance <= grade.getDistance()) {
                return grade;
            }
        }
        throw new DomainException(ErrorCode.DELIVERY_DISTANCE_NOT_FOUND);
    }

    public static DeliveryDistance resolve(
            List<DeliveryDistance> grades,
            BigDecimal buyerLat, BigDecimal buyerLon,
            BigDecimal sellerLat, BigDecimal sellerLon
    ) {
        int distance = calculateDistance(buyerLat, buyerLon, sellerLat, sellerLon);
        return findByDistance(grades, distance);
    }
}