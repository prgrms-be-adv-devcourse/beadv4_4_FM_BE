package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.in.dto.response.BuyerSummaryResponse;
import com.mossy.boundedContext.in.dto.response.SellerSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserFacade {

    private final GetAdminUsersUseCase getAdminUsersUseCase;
    private final SuspendUserUseCase suspendUserUseCase;

    public Page<BuyerSummaryResponse> getBuyers(Pageable pageable) {
        return getAdminUsersUseCase.getBuyers(pageable);
    }

    public Page<SellerSummaryResponse> getSellers(Pageable pageable) {
        return getAdminUsersUseCase.getSellers(pageable);
    }

    public void suspendUser(Long userId) {
        suspendUserUseCase.suspend(userId);
    }

    public void activateUser(Long userId) {
        suspendUserUseCase.activate(userId);
    }
}
