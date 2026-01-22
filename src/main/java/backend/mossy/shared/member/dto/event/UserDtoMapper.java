package backend.mossy.shared.member.event;

import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.shared.member.dto.event.UserDto;

public class UserDtoMapper {

    private UserDtoMapper() {}

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}