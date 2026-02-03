package com.mossy.shared.member.event;

import com.mossy.shared.member.domain.seller.SellerStatus;
import com.mossy.shared.member.domain.seller.SellerType;
import com.mossy.shared.member.dto.event.SellerApprovedEvent;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SellerJoinedEvent(
        SellerApprovedEvent seller
){
}
