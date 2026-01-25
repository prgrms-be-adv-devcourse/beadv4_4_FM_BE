package backend.mossy.global.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseIdAndTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @CreatedDate
    @Column(updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt;
}