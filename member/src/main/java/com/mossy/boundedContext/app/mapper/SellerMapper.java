package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.payload.SellerPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SellerMapper {

    @Mapping(source = "id", target = "sellerId")
    SellerPayload toPayload(Seller seller);

    default SellerJoinedEvent toSellerJoinedEvent(Seller seller) {
        return SellerJoinedEvent.builder()
                .seller(toPayload(seller))
                .build();
    }
}

