package com.mossy.boundedContext.cash.app.mapper;

import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserCashLog;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.in.dto.command.CashSellerDto;
import com.mossy.boundedContext.cash.in.dto.command.CashUserDto;
import com.mossy.boundedContext.cash.in.dto.request.CashRefundRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserCashLogResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface CashMapper {

    // --- [EventListener에서 사용 메서드들] ---

    CashUserDto toCashUserDto(UserPayload payload);

    CashSellerDto toCashSellerDto(SellerPayload payload);

    // --- [기존 UseCase/Entity 매핑] ---

    CashUser toEntity(CashUserDto userDto);

    CashUserDto toDto(CashUser user);

    CashSeller toEntity(CashSellerDto sellerDto);

    CashSellerDto toDto(CashSeller seller);

    // --- [이벤트 기반 DTO 변환] ---

    CashRefundRequestDto toCashRefundRequestDto(PaymentCashRefundEvent event);

    // --- [조회 응답 매핑] ---

    @Mapping(target = "walletId", source = "id")
    UserWalletResponseDto toResponseDto(UserWallet wallet);

    @Mapping(target = "walletId", source = "id")
    SellerWalletResponseDto toResponseDto(SellerWallet wallet);

    UserCashLogResponseDto toResponseDto(UserCashLog log);
}