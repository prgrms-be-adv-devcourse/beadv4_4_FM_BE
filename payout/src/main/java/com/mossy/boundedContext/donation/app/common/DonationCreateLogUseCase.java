package com.mossy.boundedContext.donation.app.common;

import com.mossy.boundedContext.donation.in.dto.command.CreateDonationLogDto;
import com.mossy.boundedContext.donation.domain.DonationLog;
import com.mossy.boundedContext.donation.out.DonationLogRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.app.PayoutSupport;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * [UseCase] 기부 로그 생성을 담당하는 서비스 클래스
 * DonationFacade의 '1단계: 기부 로그 생성' 흐름에서 호출
 */
@Service
@RequiredArgsConstructor
public class DonationCreateLogUseCase {

    private final DonationLogRepository donationLogRepository;
    private final PayoutSupport payoutSupport;
    private final DonationMapper donationMapper;

    /**
     * 정산 완료 시점에 PayoutCandidateItem의 정보를 사용하여 기부 로그를 생성
     * 이미 계산된 기부금액과 탄소 배출량 정보를 활용하여 중복 계산을 방지
     *
     * @param createDonationLogDto 기부 로그 생성 DTO
     */
    @Transactional
    public void createDonationLog(CreateDonationLogDto createDonationLogDto) {
        if (createDonationLogDto.OrderItemId() == null) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
        }
        if (createDonationLogDto.donationAmount() == null || createDonationLogDto.donationAmount().signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }
        if (createDonationLogDto.carbonKg() == null || createDonationLogDto.carbonKg().signum() < 0) {
            throw new DomainException(ErrorCode.INVALID_CARBON_AMOUNT);
        }

        // 1. 기부자(구매자) 정보를 조회
        PayoutUser user = payoutSupport.findUserById(createDonationLogDto.buyerId())
                .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_USER_NOT_FOUND));

        // 3. DonationLog 엔티티를 생성하고 저장
        DonationLog donationLog = donationMapper.toEntity(user, createDonationLogDto);
        donationLogRepository.save(donationLog);
    }
}
