package com.mossy.boundedContext.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ReviewableItem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReviewableItem extends BaseIdAndTime {

    @Column(name = "order_item_id", nullable = false, unique = true)
    private Long orderItemId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "is_reviewed", nullable = false)
    private boolean reviewed;

    public void markAsReviewed() {
        this.reviewed = true;
    }
}
