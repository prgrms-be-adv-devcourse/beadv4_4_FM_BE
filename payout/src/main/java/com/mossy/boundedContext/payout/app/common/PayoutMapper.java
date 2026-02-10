package com.mossy.boundedContext.payout.app.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

public interface PayoutMapper {
    @Mapper(
            componentModel = "spring",
            unmappedTargetPolicy = ReportingPolicy.IGNORE
    )
    public interface CashMapper {

        // --- [EventListener에서 사용 메서드들] ---


        // --- [기존 UseCase/Entity 매핑] ---


        // --- [이벤트 기반 DTO 변환] ---


        // --- [조회 응답 매핑] ---

    }
}
