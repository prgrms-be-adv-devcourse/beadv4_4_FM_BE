package backend.mossy.global.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
public abstract class BaseManualIdAndTime extends BaseEntity {

    @Id
    @Column(name = "id")
    protected Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime modifyDate;

    protected BaseManualIdAndTime(Long id) {
        this.id = id;
    }
}
