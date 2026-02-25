package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.domain.seller.Seller;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.response.BuyerSummaryResponse;
import com.mossy.boundedContext.in.dto.response.SellerSummaryResponse;
import com.mossy.boundedContext.out.repository.seller.SellerRepository;
import com.mossy.boundedContext.out.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAdminUsersUseCase {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Transactional(readOnly = true)
    public Page<BuyerSummaryResponse> getBuyers(Pageable pageable) {
        return userRepository.findBuyers(pageable)
                .map(BuyerSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<SellerSummaryResponse> getSellers(Pageable pageable) {
        Page<Seller> sellers = sellerRepository.findAll(pageable);

        List<Long> userIds = sellers.stream()
                .map(Seller::getUserId)
                .collect(Collectors.toList());

        Map<Long, User> userMap = userRepository.findAllByIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return sellers.map(seller -> SellerSummaryResponse.from(seller, userMap.get(seller.getUserId())));
    }
}
