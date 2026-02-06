package com.mossy.boundedContext.cash.app.mapper;

import com.mossy.boundedContext.cash.domain.seller.CashSeller;
import com.mossy.boundedContext.cash.domain.seller.SellerWallet;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.domain.user.UserWallet;
import com.mossy.boundedContext.cash.in.dto.common.CashSellerDto;
import com.mossy.boundedContext.cash.in.dto.common.CashUserDto;
import com.mossy.boundedContext.cash.in.dto.request.CashHoldingRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.CashRefundRequestDto;
import com.mossy.boundedContext.cash.in.dto.request.UserBalanceRequestDto;
import com.mossy.boundedContext.cash.in.dto.response.SellerWalletResponseDto;
import com.mossy.boundedContext.cash.in.dto.response.UserWalletResponseDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmCashRequestDto;
import com.mossy.shared.cash.enums.UserEventType;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.OrderCashPaymentRequestEvent;
import com.mossy.shared.market.event.OrderCashPrePaymentEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import org.springframework.stereotype.Component;

@Component
public class CashMapper {

    public CashUser toEntity(CashUserDto userDto) {
        return CashUser.builder()
            .id(userDto.id())
            .email(userDto.email())
            .name(userDto.name())
            .address(userDto.address())
            .nickname(userDto.nickname())
            .profileImage(userDto.profileImage())
            .status(userDto.status())
            .latitude(userDto.latitude())
            .longitude(userDto.longitude())
            .createdAt(userDto.createdAt())
            .updatedAt(userDto.updatedAt())
            .build();
    }

    public CashSeller toEntity(CashSellerDto sellerDto) {
        return CashSeller.builder()
            .id(sellerDto.sellerId())
            .userId(sellerDto.userId())
            .sellerType(sellerDto.sellerType())
            .storeName(sellerDto.storeName())
            .businessNum(sellerDto.businessNum())
            .latitude(sellerDto.latitude())
            .longitude(sellerDto.longitude())
            .status(sellerDto.status())
            .createdAt(sellerDto.createdAt())
            .updatedAt(sellerDto.updatedAt())
            .build();
    }

    public CashUserDto toCashUserDto(UserPayload payload) {
        return CashUserDto.builder()
            .id(payload.id())
            .email(payload.email())
            .name(payload.name())
            .address(payload.address())
            .nickname(payload.nickname())
            .profileImage(payload.profileImage())
            .status(payload.status())
            .latitude(payload.latitude())
            .longitude(payload.longitude())
            .createdAt(payload.createdAt())
            .updatedAt(payload.updatedAt())
            .build();
    }

    public CashSellerDto toCashSellerDto(SellerPayload payload) {
        return CashSellerDto.builder()
            .sellerId(payload.sellerId())
            .userId(payload.userId())
            .sellerType(payload.sellerType())
            .storeName(payload.storeName())
            .businessNum(payload.businessNum())
            .latitude(payload.latitude())
            .longitude(payload.longitude())
            .status(payload.status())
            .createdAt(payload.createdAt())
            .updatedAt(payload.updatedAt())
            .build();
    }

    public PaymentConfirmCashRequestDto toPaymentConfirmCashRequestDto(OrderCashPaymentRequestEvent event) {
        return PaymentConfirmCashRequestDto.builder()
            .orderId(event.orderNo())
            .amount(event.amount())
            .payMethod(PayMethod.CASH)
            .build();
    }

    public CashHoldingRequestDto toCashHoldingRequestDto(PaymentCompletedEvent event) {
        return CashHoldingRequestDto.builder()
            .orderId(event.orderId())
            .buyerId(event.buyerId())
            .amount(event.amount())
            .payMethod(event.payMethod())
            .build();
    }

    public CashRefundRequestDto toCashRefundRequestDto(PaymentRefundEvent event) {
        return CashRefundRequestDto.builder()
            .orderId(event.orderId())
            .buyerId(event.buyerId())
            .amount(event.amount())
            .payMethod(event.payMethod())
            .build();
    }

    public UserPayload toPayload(CashUser user) {
        return UserPayload.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .address(user.getAddress())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .status(user.getStatus())
            .latitude(user.getLatitude())
            .longitude(user.getLongitude())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public SellerPayload toPayload(CashSeller seller) {
        return SellerPayload.builder()
            .sellerId(seller.getId())
            .userId(seller.getUserId())
            .sellerType(seller.getSellerType())
            .storeName(seller.getStoreName())
            .businessNum(seller.getBusinessNum())
            .latitude(seller.getLatitude())
            .longitude(seller.getLongitude())
            .status(seller.getStatus())
            .createdAt(seller.getCreatedAt())
            .updatedAt(seller.getUpdatedAt())
            .build();
    }

    public CashUserDto toDto(CashUser user) {
        return CashUserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .address(user.getAddress())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .status(user.getStatus())
            .latitude(user.getLatitude())
            .longitude(user.getLongitude())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public CashSellerDto toDto(CashSeller seller) {
        return CashSellerDto.builder()
            .sellerId(seller.getId())
            .userId(seller.getUserId())
            .sellerType(seller.getSellerType())
            .storeName(seller.getStoreName())
            .businessNum(seller.getBusinessNum())
            .latitude(seller.getLatitude())
            .longitude(seller.getLongitude())
            .status(seller.getStatus())
            .createdAt(seller.getCreatedAt())
            .updatedAt(seller.getUpdatedAt())
            .build();
    }

    public UserWalletResponseDto toResponseDto(UserWallet wallet) {
        return UserWalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .user(toDto(wallet.getUser()))
            .build();
    }

    public SellerWalletResponseDto toResponseDto(SellerWallet wallet) {
        return SellerWalletResponseDto.builder()
            .walletId(wallet.getId())
            .balance(wallet.getBalance())
            .seller(toDto(wallet.getSeller()))
            .build();
    }

    public UserBalanceRequestDto toUserBalanceRequestDto(OrderCashPrePaymentEvent event) {
        return UserBalanceRequestDto.builder()
            .userId(event.buyerId())
            .amount(event.amount())
            .eventType(UserEventType.사용__주문결제)
            .relTypeCode("ORDER")
            .relId(event.orderId())
            .build();
    }
}