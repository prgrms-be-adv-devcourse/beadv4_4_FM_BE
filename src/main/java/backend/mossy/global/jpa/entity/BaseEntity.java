package backend.mossy.global.jpa.entity;

import backend.mossy.global.global.GlobalConfig;
import backend.mossy.standard.modelType.HashModelTypeCode;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseEntity implements HashModelTypeCode {

    @Override
    public String getModelTypeCode() {
        return this.getClass().getSimpleName();
    }

    protected void publishEvent(Object event) {
        GlobalConfig.getEventPublisher().publish(event);
    }
}
