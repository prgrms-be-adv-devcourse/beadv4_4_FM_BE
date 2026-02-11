package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.in.dto.command.CreatePayoutCandidateDto;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateItemCreateDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutSellerDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutUserDto;
import com.mossy.shared.market.event.OrderPaidEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PayoutMapper {
        // --- [EventListener에서 사용 메서드들] ---

        // --- [기존 UseCase/Entity 매핑] ---
        @Mapping(target = "id", source = "sellerId")
        PayoutSeller toEntity(PayoutSellerDto dto);
        
        PayoutUser toEntity(PayoutUserDto dto);

        @Mapping(target = "relTypeCode", constant = "OrderItem")
        @Mapping(target = "relId", source = "orderItem.orderItemId")
        PayoutCandidateItem toEntity(PayoutCandidateItemCreateDto dto);

        // --- [이벤트 기반 DTO 변환] ---
        @Mapping(target = "orderItemId", source = "orderItem.orderItemId")
        @Mapping(target = "buyerId", source = "event.buyerId")
        @Mapping(target = "buyerName", source = "event.buyerName")
        @Mapping(target = "sellerId", source = "orderItem.sellerId")
        @Mapping(target = "orderPrice", source = "orderItem.orderPrice")
        @Mapping(target = "orderItemCreatedAt", source = "orderItem.createdAt")
        @Mapping(target = "orderItemUpdatedAt", source = "orderItem.updatedAt")
        @Mapping(target = "weightGrade", constant = "소형")
        @Mapping(target = "deliveryDistance", source = "deliveryDistance")
        @Mapping(target = "paymentDate", source = "event.createdAt")
        CreatePayoutCandidateDto toCreatePayoutCandidateDto(
                OrderPaidEvent event,
                OrderPaidEvent.OrderItem orderItem,
                BigDecimal deliveryDistance
        );


        // --- [조회 응답 매핑] ---


}
