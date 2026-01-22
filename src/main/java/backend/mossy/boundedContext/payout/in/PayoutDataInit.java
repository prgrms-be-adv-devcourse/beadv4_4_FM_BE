package backend.mossy.boundedContext.payout.in;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.boundedContext.payout.app.payout.MarketApiClient;
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
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.JobOperator;
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

/**
 * [Input Adapter - Data Initializer] Payout 컨텍스트의 개발 및 테스트 환경을 위한 초기 데이터 설정 클래스입니다.
 * 애플리케이션 시작 시 자동으로 실행되어 필요한 테스트 데이터(사용자, 판매자, 주문)를 생성하고,
 * 정산 및 기부 관련 배치 프로세스의 초기 단계를 수동으로 트리거하여 전체 흐름을 테스트할 수 있도록 돕습니다.
 */
@Configuration
@Slf4j
public class PayoutDataInit {
    private final PayoutDataInit self; // 재귀 호출을 위한 프록시 (AOP 트랜잭션 적용 목적)
    private final PayoutFacade payoutFacade;
    private final DonationFacade donationFacade;
    private final MarketApiClient marketApiClient;
    private final JobOperator jobOperator; // Spring Batch Job을 수동으로 실행하기 위한 오퍼레이터
    private Job payoutCollectItemsAndCompletePayoutsJob; // 실행할 배치 Job (주입 예정)

    /**
     * PayoutDataInit 생성자. 필요한 의존성을 주입받습니다.
     * {@literal @Lazy} 어노테이션은 `self` 주입 시 발생할 수 있는 순환 참조를 방지합니다.
     */
    public PayoutDataInit(
            @Lazy PayoutDataInit self, // 자기 자신을 @Lazy로 주입받아 AOP(트랜잭션) 적용
            PayoutFacade payoutFacade,
            DonationFacade donationFacade,
            MarketApiClient marketApiClient,
            JobOperator jobOperator
    ) {
        this.self = self;
        this.payoutFacade = payoutFacade;
        this.donationFacade = donationFacade;
        this.marketApiClient = marketApiClient;
        this.jobOperator = jobOperator;
        this.payoutCollectItemsAndCompletePayoutsJob = null; // 실제 Job은 `@Autowired` 또는 `@Bean`으로 주입받아야 함
    }

    /**
     * 애플리케이션 시작 시 실행되는 ApplicationRunner 빈을 정의합니다.
     * {@literal @Order(4)}를 통해 실행 순서를 제어하며, 테스트 데이터 생성 및 정산 프로세스를 트리거합니다.
     *
     * @return 테스트 데이터를 초기화하는 ApplicationRunner
     */
    @Bean
    @Order(4)
    public ApplicationRunner payoutDataInitApplicationRunner() {
        return args -> {
            // 1. 테스트 데이터 생성
            self.createTestData();
            // 2. 정산 후보 아이템 집계 (Flow 2 수동 실행)
            self.collectPayoutItemsMore();
            // 3. 정산 완료 처리 (Flow 3 수동 실행)
            self.completePayoutsMore();
            // 4. (선택 사항) Spring Batch Job을 직접 실행하는 예시
            // self.runCollectItemsAndCompletePayoutsBatchJob();
        };
    }

    /**
     * [정산 플로우 2단계 수동 트리거] 정산 후보 아이템들을 실제 정산(Payout)에 포함될 아이템으로 집계합니다.
     * {@link PayoutFacade#collectPayoutItemsMore(int)}를 호출합니다.
     */
    @Transactional
    public void collectPayoutItemsMore() {
        // 모든 정산 후보를 처리하기 위해 충분히 큰 limit 값을 사용합니다.
        payoutFacade.collectPayoutItemsMore(100);
    }

    /**
     * [정산 플로우 3단계 수동 트리거] 생성된 정산(Payout)들을 실제 실행하고 완료 처리합니다.
     * {@link PayoutFacade#completePayoutsMore(int)}를 호출합니다.
     */
    @Transactional
    public void completePayoutsMore() {
        // 모든 Payout을 완료 처리하기 위해 적절한 limit 값을 사용합니다.
        payoutFacade.completePayoutsMore(10);
    }

    /**
     * Spring Batch Job을 {@link JobOperator}를 통해 수동으로 실행하는 예시 메서드입니다.
     * 이 메서드는 {@literal ApplicationRunner}에서 주석 처리되어 있으며, 필요 시 활성화하여 사용할 수 있습니다.
     */
    public void runCollectItemsAndCompletePayoutsBatchJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(
                        "runDate",
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                .toJobParameters();

        try {
            // jobOperator를 사용하여 배치 Job을 시작합니다.
            JobExecution execution = jobOperator.start(payoutCollectItemsAndCompletePayoutsJob, jobParameters);
            log.info("Batch Job '{}' started with ID: {}", payoutCollectItemsAndCompletePayoutsJob.getName(), execution.getId());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("배치 작업이 이미 완료되었습니다.", e);
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("배치 작업이 이미 실행 중입니다.", e);
        } catch (InvalidJobParametersException e) {
            log.error("잘못된 배치 작업 파라미터입니다.", e);
        } catch (JobRestartException e) {
            log.error("배치 작업 재시작 중 예외가 발생했습니다.", e);
        } catch (Exception e) { // start() 메서드는 JobParametersInvalidException 외에 일반 Exception도 던질 수 있음
            log.error("배치 작업 실행 중 알 수 없는 예외가 발생했습니다.", e);
        }
    }

    /**
     * Payout 및 Donation 기능 테스트를 위한 초기 데이터를 생성합니다.
     *
     * <p>생성 단계:</p>
     * <ol>
     *     <li>**시스템 Seller 생성**: 플랫폼 수수료 및 기부금 수취용 가상 판매자를 생성합니다.</li>
     *     <li>**기부금 수령자 Seller 생성**: 기부금을 수령하는 가상 판매자를 생성합니다.</li>
     *     <li>**구매자 User 생성**: 테스트용 일반 구매자 사용자를 생성합니다.</li>
     *     <li>**판매자 Seller 생성**: 실제 상품 판매를 위한 일반 판매자들을 생성하고, 각 판매자에 대한 초기 Payout 객체를 생성합니다.</li>
     *     <li>**주문 데이터 생성 및 정산/기부 후보 생성**: 과거 날짜의 가상 주문 데이터를 생성하고, 이를 기반으로 정산 후보 아이템과 기부 로그를 생성합니다.</li>
     * </ol>
     */
    @Transactional
    public void createTestData() {
        log.info("===== 테스트 데이터 생성 시작 =====");

        // 1. 시스템 Seller 생성 및 동기화 (payoutFacade의 syncSeller 사용)
        SellerDto systemSeller = SellerDto.builder()
                .id(1L)
                .userId(1L)
                .sellerType(SellerType.SYSTEM)
                .storeName("system")  // PayoutSeller.isSystem() 메서드에서 "system"과 비교
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

        // 2. 기부금 수령자 Seller 생성 및 동기화
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

        // 3. 구매자 User 생성 및 동기화
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

        // 4. 일반 판매자 Seller 생성 및 동기화, 초기 Payout 생성
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
        // 각 판매자에 대해 수동으로 Payout 객체를 하나 생성 (배치 처리에 사용될 Payout을 미리 준비)
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
        // 각 판매자에 대해 수동으로 Payout 객체를 하나 생성
        payoutFacade.createPayout(seller2.id());
        log.info("판매자2 Seller 생성 완료: ID={}", seller2.id());

        // 5. 주문 데이터 생성 및 정산/기부 후보 생성
        // PayoutPolicy.PAYOUT_READY_WAITING_DAYS + 1 일 전으로 설정하여,
        // 이 주문에 대한 정산 후보 아이템이 바로 정산 준비 상태가 되도록 합니다.
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
        // 정산 후보 항목 생성 (Payout Flow 1단계)
        payoutFacade.addPayoutCandidateItems(order);
        log.info("주문 정산 후보 생성 완료: OrderID={}, PaymentDate={}", order.id(), pastPaymentDate);

        // 기부 로그 생성 (Donation Flow 1단계)
        // MarketApiClient를 통해 Mock 주문 아이템들을 가져와서 각 아이템에 대한 기부 로그를 생성합니다.
        marketApiClient.getOrderItems(order.id())
                .forEach(orderItem -> donationFacade.createDonationLog(order, orderItem));
        log.info("기부 로그 생성 완료: OrderID={}", order.id());

        log.info("===== 테스트 데이터 생성 완료 =====");
    }
}
