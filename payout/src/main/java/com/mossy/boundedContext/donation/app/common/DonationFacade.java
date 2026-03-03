package com.mossy.boundedContext.donation.app.common;

import com.mossy.boundedContext.donation.in.dto.command.CreateDonationLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기부(Donation) 기능의 메인 진입점 역할을 하는 파사드(Facade)
 * 기부와 관련된 핵심 비즈니스 로직(Use Case)들을 외부 또는 내부 모듈에서 쉽게 사용할 수 있도록 캡슐화
 * <p>
 * 기부 플로우:
 * 1. 정산 완료 시 DonationLogCreateEvent 수신
 * 2. 기부 로그 생성 (이미 정산 완료 상태)
 */
@Service
@RequiredArgsConstructor
public class DonationFacade {

    private final DonationCreateLogUseCase donationCreateLogUseCase;

    /**
     * 정산 완료 시점에 기부 로그를 생성
     * PayoutCandidateItem의 정보를 이벤트로 전달받아 기부 로그를 생성
     * 이미 계산된 기부금액과 탄소 배출량 정보를 활용하여 중복 계산을 방지
     *
     * @param createDonationLogDto 기부 로그 생성 DTO
     */
    @Transactional
    public void createDonationLog(CreateDonationLogDto createDonationLogDto) {
        donationCreateLogUseCase.createDonationLog(createDonationLogDto);
    }

}