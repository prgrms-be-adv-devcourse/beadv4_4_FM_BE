package com.mossy.boundedContext.payment.app.mapper;

import com.mossy.boundedContext.cash.in.dto.request.CashHoldingRequestDto;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.command.PaymentCompletedDto;
import com.mossy.boundedContext.payment.in.dto.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentMapper {

    @Mapping(target = "paymentId", source = "id")
    PaymentResponse toPaymentResponse(Payment payment);

    CashHoldingRequestDto toCashHoldingRequestDto(PaymentCompletedDto dto);
}
