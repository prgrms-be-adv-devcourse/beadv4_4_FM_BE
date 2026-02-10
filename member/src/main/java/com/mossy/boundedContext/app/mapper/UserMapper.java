package com.mossy.boundedContext.app.mapper;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.payload.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserPayload toPayload(User user);
}
