package com.mossy.boundedContext.donation.app.common;
import com.mossy.boundedContext.donation.domain.DonationLog;
import com.mossy.boundedContext.donation.in.dto.command.CreateDonationLogDto;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.out.external.dto.event.DonationLogCreateEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DonationMapper {

    // Event → DTO 변환
    CreateDonationLogDto toDto(DonationLogCreateEvent event);

    // DTO → Entity 변환
    @Mapping(target = "user", source = "user")
    @Mapping(target = "orderItemId", source = "dto.OrderItemId")
    @Mapping(target = "amount", source = "dto.donationAmount")
    @Mapping(target = "carbonOffset", source = "dto.carbonKg")
    DonationLog toEntity(PayoutUser user, CreateDonationLogDto dto);
}