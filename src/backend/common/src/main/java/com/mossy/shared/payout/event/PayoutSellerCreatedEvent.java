package com.mossy.shared.payout.event;

import com.mossy.shared.member.dto.event.SellerApprovedEvent;


public record PayoutSellerCreatedEvent(SellerApprovedEvent seller) {
}
