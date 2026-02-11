package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.payload.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserPayload toPayload(User user);
}
