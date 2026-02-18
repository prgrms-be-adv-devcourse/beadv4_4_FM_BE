package com.mossy.boundedContext.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Review")
@SQLRestriction("status != 'DELETED'")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AttributeOverride(name = "id", column = @Column(name = "review_id"))
public class Review extends BaseIdAndTime {
}
