package com.mossy.boundedContext.donation.in;


import com.mossy.boundedContext.donation.app.common.DonationFacade;
import com.mossy.boundedContext.donation.app.common.DonationMapper;
import com.mossy.boundedContext.donation.in.dto.command.CreateDonationLogDto;
import com.mossy.boundedContext.payout.out.external.dto.event.DonationLogCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * [Inbound Adapter] 기부 로그 생성 이벤트를 수신하여 기부 로그를 생성하는 리스너 클래스
 * Payout 도메인에서 정산 완료 시 DonationLogCreateEvent를 발행하면,
 * 이 리스너가 해당 이벤트를 수신하여 기부 로그를 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DonationEventListener {

    private final DonationFacade donationFacade;
    private final DonationMapper donationMapper;

    /**
     * DonationLogCreateEvent를 수신하여 기부 로그를 생성
     * Payout 도메인에서 정산 완료 시 기부금 정보를 이벤트로 전달받아 기부 로그를 생성
     * 기부 로그가 생성되는 시점에 이미 정산이 완료된 상태
     *
     * @param event 기부 로그 생성 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onDonationLogCreate(DonationLogCreateEvent event) {
        log.info("기부 로그 생성 이벤트 수신 (OrderItem ID: {})", event.orderItemId());
        CreateDonationLogDto createDonationLogDto = donationMapper.toDto(event);
        donationFacade.createDonationLog(createDonationLogDto);
        log.info("기부 로그가 생성되었습니다 (OrderItem ID: {})", event.orderItemId());
    }
}