package backend.mossy.shared.member.event;

import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SellerJoinedEvent(
        SellerApprovedEvent seller
){
}
