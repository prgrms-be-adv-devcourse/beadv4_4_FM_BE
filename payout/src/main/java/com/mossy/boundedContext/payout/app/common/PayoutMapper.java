package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateItemCreateDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutSellerDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PayoutMapper {
        // --- [EventListener에서 사용 메서드들] ---

        // --- [기존 UseCase/Entity 매핑] ---
        @Mapping(target = "id", source = "sellerId")
        PayoutSeller toEntity(PayoutSellerDto dto);

        @Mapping(target = "id", source = "id")
        PayoutUser toEntity(PayoutUserDto dto);

        @Mapping(target = "relTypeCode", constant = "OrderItem")
        @Mapping(target = "relId", source = "orderItem.id")
        PayoutCandidateItem toEntity(PayoutCandidateItemCreateDto dto);

        // --- [이벤트 기반 DTO 변환] ---


        // --- [조회 응답 매핑] ---


}
