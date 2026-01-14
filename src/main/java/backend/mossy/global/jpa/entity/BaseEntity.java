package backend.mossy.global.jpa.entity;

import backend.mossy.global.global.GlobalConfig;
import backend.mossy.standard.modelType.HashModelTypeCode;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
// 모든 엔티티들의 조상
public abstract class BaseEntity implements HashModelTypeCode {
    @Override
    public String getModelTypeCode() {
        return this.getClass().getSimpleName();
    }

    protected void publishEvent(Object event) {
        GlobalConfig.getEventPublisher().publish(event);
    }

    public abstract Long getId();
    public abstract LocalDateTime getCreateDate();
    public abstract LocalDateTime getModifyDate();
}