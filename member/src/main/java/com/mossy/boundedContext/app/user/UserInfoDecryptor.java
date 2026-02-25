package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.global.ut.EncryptionUtils;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.shared.member.domain.enums.SellerRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoDecryptor {

    private final UserMapper userMapper;
    private final EncryptionUtils encryptionUtils;

    public UserInfoDto decryptUserInfo(User user, SellerRequestStatus status) {
        // mapper에서 기본 변환
        UserInfoDto dto = userMapper.toUserInfoDto(user, status);

        // 민감정보 복호화
        String decryptedRrn = decryptIfNotEmpty(dto.rrn());
        String decryptedPhoneNum = decryptIfNotEmpty(dto.phoneNum());
        String decryptedAddress = decryptIfNotEmpty(dto.address());

        // 복호화된 데이터로 새로운 DTO 생성
        return new UserInfoDto(
                dto.userId(),
                dto.nickname(),
                dto.email(),
                dto.username(),
                dto.profileImage(),
                decryptedRrn,
                decryptedPhoneNum,
                decryptedAddress,
                dto.status(),
                dto.providers(),
                dto.hasPassword()
        );
    }

    private String decryptIfNotEmpty(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return "";
        }
        try {
            return encryptionUtils.decrypt(encryptedValue);
        } catch (Exception e) {
            // 복호화 실패 시 빈 문자열 반환 (보안상 암호화된 데이터 노출 방지)
            return "";
        }
    }
}

