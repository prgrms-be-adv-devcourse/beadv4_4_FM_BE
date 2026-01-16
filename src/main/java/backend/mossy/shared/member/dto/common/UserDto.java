package backend.mossy.shared.member.dto.common;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserDto(
    Long id,                // 유저 고유 식별자
    LocalDateTime createdAt, // 생성일
    LocalDateTime updatedAt, // 수정일
    String email,           // 이메일
    String name,            // 이름
    String nickname,        // 닉네임
    String address,         // 주소
    String status           // 유저 상태
) {}