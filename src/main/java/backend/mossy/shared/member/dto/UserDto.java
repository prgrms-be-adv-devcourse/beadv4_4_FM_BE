package backend.mossy.shared.member.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto (
        Long id,
        String email,
        String name,
        String rrnEncrypted,
        String phoneNum,
        String password,
        String address,
        String status,
        String nickname,
        String profileImage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){ }
