package backend.mossy.boundedContext.payout.in;

import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.boundedContext.payout.domain.payout.PayoutPolicy;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.event.SellerDto;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import backend.mossy.shared.member.domain.seller.SellerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class PayoutDataInit {
    private final PayoutDataInit self;
    private final PayoutFacade payoutFacade;
    private final JobOperator jobOperator;
    private Job payoutCollectItemsAndCompletePayoutsJob;

    public PayoutDataInit(
            @Lazy PayoutDataInit self,
            PayoutFacade payoutFacade,
            JobOperator jobOperator
    ) {
        this.self = self;
        this.payoutFacade = payoutFacade;
        this.jobOperator = jobOperator;
        this.payoutCollectItemsAndCompletePayoutsJob = null;
    }

    @Bean
    @Order(4)
    public ApplicationRunner payoutDataInitApplicationRunner() {
        return args -> {
            self.createTestData();
            self.collectPayoutItemsMore();
            self.completePayoutsMore();
            // self.runCollectItemsAndCompletePayoutsBatchJob();
        };
    }

    @Transactional
    public void collectPayoutItemsMore() {
        payoutFacade.collectPayoutItemsMore(2);
    }

    @Transactional
    public void completePayoutsMore() {
        payoutFacade.completePayoutsMore(1);
    }

    public void runCollectItemsAndCompletePayoutsBatchJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(
                        "runDate",
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                .toJobParameters();

        try {
            JobExecution execution = jobOperator.start(payoutCollectItemsAndCompletePayoutsJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job instance already complete", e);
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job execution already running", e);
        } catch (InvalidJobParametersException e) {
            log.error("Invalid job parameters", e);
        } catch (JobRestartException e) {
            log.error("job restart exception", e);
        }
    }

    @Transactional
    public void createTestData() {
        log.info("===== 테스트 데이터 생성 시작 =====");

        // 1. 시스템 Seller 생성
        SellerDto systemSeller = SellerDto.builder()
                .id(1L)
                .userId(1L)
                .sellerType(SellerType.SYSTEM)
                .storeName("system")  // isSystem() 메서드에서 "system"과 비교
                .businessNum("0")
                .representativeName("시스템")
                .contactEmail("system@ssys.com")
                .contactPhone("010-0000-0000")
                .address1("콤퓨타시")
                .address2("램어딘가")
                .status(SellerStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        payoutFacade.syncSeller(systemSeller);
        log.info("시스템 Seller 생성 완료: ID={}", systemSeller.id());

        // 2. 기부금 수령자 Seller 생성
        SellerDto donationSeller = SellerDto.builder()
                .id(2L)
                .userId(2L)
                .sellerType(SellerType.SYSTEM)
                .storeName("DONATION")
                .businessNum("DONATION-0")
                .representativeName("기부금수령자")
                .contactEmail("donation@ssys.com")
                .contactPhone("010-0000-0001")
                .address1("콤퓨타시")
                .address2("기부어딘가")
                .status(SellerStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        payoutFacade.syncSeller(donationSeller);
        log.info("기부금 수령자 Seller 생성 완료: ID={}", donationSeller.id());

        // 3. 구매자 User 생성
        UserDto buyerUser = UserDto.builder()
                .id(1000L)
                .email("buyer@example.com")
                .name("김구매")
                .address("서울시 강남구 101호")
                .nickname("구매자닉네임")
                .profileImage("https://example.com/profile.jpg")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        payoutFacade.syncUser(buyerUser);
        log.info("구매자 User 생성 완료: ID={}", buyerUser.id());

        // 4. 판매자 Seller 생성
        SellerDto seller1 = SellerDto.builder()
                .id(200L)
                .userId(2000L)
                .sellerType(SellerType.BUSINESS)
                .storeName("판매자상점1")
                .businessNum("234-56-78901")
                .representativeName("김판매")
                .contactEmail("seller1@example.com")
                .contactPhone("010-3333-4444")
                .address1("서울시 서초구")
                .address2("202호")
                .status(SellerStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        payoutFacade.syncSeller(seller1);
        payoutFacade.createPayout(seller1.id());
        log.info("판매자1 Seller 생성 완료: ID={}", seller1.id());

        SellerDto seller2 = SellerDto.builder()
                .id(300L)
                .userId(3000L)
                .sellerType(SellerType.INDIVIDUAL)
                .storeName("판매자상점2")
                .businessNum("345-67-89012")
                .representativeName("박판매")
                .contactEmail("seller2@example.com")
                .contactPhone("010-5555-6666")
                .address1("경기도 성남시")
                .address2("303호")
                .status(SellerStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        payoutFacade.syncSeller(seller2);
        payoutFacade.createPayout(seller2.id());
        log.info("판매자2 Seller 생성 완료: ID={}", seller2.id());

        // 5. 주문 데이터 생성 및 정산 후보 생성
        LocalDateTime pastPaymentDate = LocalDateTime.now().minusDays(PayoutPolicy.PAYOUT_READY_WAITING_DAYS + 1);
        OrderDto order = new OrderDto(
                1001L,                          // id
                LocalDateTime.now(),            // createdAt
                LocalDateTime.now(),            // updatedAt
                1000L,                          // customerId (buyer의 userId)
                "구매자상점",                      // customerName
                new BigDecimal("15000"),        // price (총 원가)
                new BigDecimal("15000"),        // salePrice (총 판매가)
                pastPaymentDate,                // requestPaymentDate (과거 날짜)
                pastPaymentDate                 // paymentDate (과거 날짜)
        );
        payoutFacade.addPayoutCandidateItems(order);
        log.info("주문 정산 후보 생성 완료: OrderID={}, PaymentDate={}", order.id(), pastPaymentDate);

        log.info("===== 테스트 데이터 생성 완료 =====");
    }
}
