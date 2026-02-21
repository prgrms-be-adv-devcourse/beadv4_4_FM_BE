package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.boundedContext.in.dto.response.SellerRequestListDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SellerRequestMapper {

    SellerRequestListDto toSellerRequestListDto(SellerRequest sellerRequest);

    List<SellerRequestListDto> toSellerRequestListDtos(List<SellerRequest> sellerRequests);
}

