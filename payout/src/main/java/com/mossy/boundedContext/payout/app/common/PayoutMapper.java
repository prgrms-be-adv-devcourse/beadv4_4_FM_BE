package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateItemCreateDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutSellerDto;
import com.mossy.boundedContext.payout.in.dto.event.PayoutUserDto;
import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.IssuerType;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import com.mossy.shared.payout.enums.PayoutEventType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PayoutMapper {
        // --- [EventListener에서 사용 메서드들] ---

        // --- [Payload to DTO 변환] ---
        PayoutSellerDto toDto(SellerPayload seller);

        PayoutUserDto toDto(UserPayload user);

        // --- [기존 UseCase/Entity 매핑] ---
        @Mapping(target = "id", source = "sellerId")
        PayoutSeller toEntity(PayoutSellerDto dto);

        PayoutUser toEntity(PayoutUserDto dto);

        @Mapping(target = "sellerId", source = "id")
        SellerPayload toPayload(PayoutSeller seller);

        UserPayload toPayload(PayoutUser user);




        @Mapping(target = "relTypeCode", constant = "OrderItem")
        //@Mapping(target =="relId", source = "orderItem.orderItemId")
        PayoutCandidateItem toEntity(PayoutCandidateItemCreateDto dto);

        @Mapping(target = "paymentDate", source = "dto.paymentDate")
        @Mapping(target = "orderItem", source = "dto")
        @Mapping(target = "eventType", source = "eventType")
        @Mapping(target = "payer", source = "payer")
        @Mapping(target = "payee", source = "payee")
        @Mapping(target = "amount", source = "amount")
        @Mapping(target = "weightGrade", source = "dto.weightGrade")
        @Mapping(target = "deliveryDistance", source = "dto.deliveryDistance")
        @Mapping(target = "carbonKg", source = "carbonKg")
        PayoutCandidateItemCreateDto toCandidateItemCreateDto(
                PayoutCandidateCreateDto dto,
                PayoutEventType eventType,
                PayoutUser payer,
                PayoutSeller payee,
                BigDecimal amount,
                BigDecimal carbonKg
        );

        // --- [이벤트 기반 DTO 변환] ---
        @Mapping(target = "orderItemId", source = "orderItem.orderItemId")
        @Mapping(target = "buyerId", source = "event.buyerId")
        @Mapping(target = "sellerId", source = "orderItem.sellerId")
        @Mapping(target = "orderPrice", source = "orderItem.finalPrice")
        @Mapping(target = "originalPrice", source = "orderItem.originalPrice")
        @Mapping(target = "platformDiscountAmount", expression = "java(calculatePlatformDiscountAmount(orderItem))")
        @Mapping(target = "paymentDate", source = "event.paidAt")
        PayoutCandidateCreateDto toCreatePayoutCandidateDto(
                OrderPurchaseConfirmedEvent event,
                OrderPurchaseConfirmedEvent.OrderItemPayload orderItem,
                BigDecimal deliveryDistance,
                String weightGrade
        );

        /**
         * 플랫폼(ADMIN) 쿠폰일 때만 실제 할인 금액을 계산하여 반환.
         * - FIXED: discountAmount 자체가 고정 할인 금액
         * - PERCENTAGE: originalPrice × (discountAmount / 100) 으로 계산
         * 판매자(SELLER) 쿠폰은 finalPrice에 이미 반영되어 있으므로 0 반환.
         */
        default BigDecimal calculatePlatformDiscountAmount(
                OrderPurchaseConfirmedEvent.OrderItemPayload orderItem
        ) {
            if (orderItem.issuerType() != IssuerType.ADMIN || orderItem.discountAmount() == null) {
                return BigDecimal.ZERO;
            }
            if (orderItem.couponType() == CouponType.FIXED) {
                return orderItem.discountAmount();
            }
            // PERCENTAGE: originalPrice × rate / 100
            return orderItem.originalPrice()
                    .multiply(orderItem.discountAmount())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        }


        // --- [조회 응답 매핑] ---


        //---[환불 처리 매핑] ---
        @Mapping(target = "eventType", source = "refundEventType")
        @Mapping(target = "relTypeCode", source = "original.relTypeCode")
        @Mapping(target = "relId", source = "original.relId")
        @Mapping(target = "paymentDate", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "payer", source = "original.payer")
        @Mapping(target = "payee", source = "original.payee")
        @Mapping(target = "amount", source = "refundAmount")
        @Mapping(target = "weightGrade", source = "original.weightGrade")
        @Mapping(target = "deliveryDistance", source = "original.deliveryDistance")
        @Mapping(target = "carbonKg", source = "refundCarbon")
        PayoutCandidateItem createRefundItem(
                PayoutCandidateItem original,
                PayoutEventType refundEventType,
                BigDecimal refundAmount,
                BigDecimal refundCarbon
        );

}
