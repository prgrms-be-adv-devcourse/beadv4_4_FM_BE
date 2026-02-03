package com.mossy.global.jpa.entity;

import com.mossy.global.config.GlobalConfig;
import com.mossy.standard.modelType.HashModelTypeCode;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity implements HashModelTypeCode {
    public abstract Long getId();
    public abstract LocalDateTime getCreatedAt();
    public abstract LocalDateTime getUpdatedAt();

    public String getModelTypeCode() {
        return this.getClass().getSimpleName();
    }

    protected void publishEvent(Object event) {
        GlobalConfig.getEventPublisher().publish(event);
    }
}
