package com.mossy.member.app.usecase.user;

import com.mossy.member.out.user.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashGetWalletInfoUseCase {

    private final UserWalletRepository userWalletRepository;

//    public UserWalletResponseDto getUserWalletInfo(Long userId) {
//        return userWalletRepository.findWalletByUserId(userId)
//            .map(UserWalletResponseDto::from)
//            .orElseThrow(
//                () -> new DomainException("NOT_FOUND_USER_WALLET", "회원의 지갑 정보를 찾을 수 없습니다." + userId));
//    }
}
