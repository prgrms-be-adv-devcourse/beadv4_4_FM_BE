package com.mossy.boundedContext.cash.app.mapper;

import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserCashLog;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.in.dto.common.CashSellerDto;
import com.mossy.boundedContext.cash.in.dto.common.CashUserDto;
import com.mossy.boundedContext.cash.in.dto.request.CashHoldingRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.CashRefundRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.OrderCashPrePaymentEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CashMapper {

    // --- [EventListener에서 사용 메서드들] ---

    CashUserDto toCashUserDto(UserPayload payload);

    @Mapping(target = "sellerId", source = "sellerId")
    CashSellerDto toCashSellerDto(SellerPayload payload);

    // --- [기존 UseCase/Entity 매핑] ---

    CashUser toEntity(CashUserDto userDto);
    CashUserDto toDto(CashUser user);

    @Mapping(target = "id", source = "sellerId")
    CashSeller toEntity(CashSellerDto sellerDto);

    @Mapping(target = "sellerId", source = "id")
    CashSellerDto toDto(CashSeller seller);

    UserPayload toPayload(CashUser user);

    @Mapping(target = "sellerId", source = "id")
    SellerPayload toPayload(CashSeller seller);

    // --- [이벤트 기반 DTO 변환] ---

    @Mapping(target = "userId", source = "buyerId")
    @Mapping(target = "relId", source = "orderId")
    @Mapping(target = "eventType", expression = "java(com.mossy.shared.cash.enums.UserEventType.사용__주문결제)")
    @Mapping(target = "relTypeCode", constant = "ORDER")
    UserBalanceRequestDto toUserBalanceRequestDto(OrderCashPrePaymentEvent event);

    CashHoldingRequestDto toCashHoldingRequestDto(PaymentCompletedEvent event);

    CashRefundRequestDto toCashRefundRequestDto(PaymentRefundEvent event);

    // --- [조회 응답 매핑] ---

    @Mapping(target = "walletId", source = "id")
    UserWalletResponseDto toResponseDto(UserWallet wallet);

    @Mapping(target = "walletId", source = "id")
    SellerWalletResponseDto toResponseDto(SellerWallet wallet);

    UserCashLogResponseDto toResponseDto(UserCashLog log);
}