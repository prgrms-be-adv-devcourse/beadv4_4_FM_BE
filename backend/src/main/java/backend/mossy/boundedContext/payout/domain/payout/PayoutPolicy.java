package backend.mossy.boundedContext.payout.domain.payout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * [Domain Policy] 정산(Payout) 관련 정책 및 규칙을 정의하는 클래스
 * 특히 정산 후보 아이템이 실제 정산 대상으로 간주되기까지 필요한 대기 기간과 같은
 * 비즈니스 정책 값을 설정하고 관리. Spring의 {@literal @Value} 어노테이션을 통해 외부 설정값을 주입받음
 */
@Configuration
public class PayoutPolicy {

    /**
     * 정산 후보 아이템이 생성된 후 실제 정산(Payout) 대상으로 처리될 때까지의 최소 대기 일수
     * 이 기간이 지나야 정산 후보 아이템이 Payout에 집계 될 수 있음 (기본값: 7일)
     */
    public static int PAYOUT_READY_WAITING_DAYS;

    /**
     * 외부 설정(application.yml 등)으로부터 정산 준비 대기 일수를 주입받는 setter 메서드
     * {@literal ${custom.payout.readyWaitingDays}} 값으로 설정하며, 기본값은 7일
     * @param payoutReadyWaitingDays 설정 파일로부터 주입될 정산 준비 대기 일수
     */
    @Value("${custom.payout.readyWaitingDays:7}")
    public void setPayoutReadyWaitingDays(int payoutReadyWaitingDays) {
        PAYOUT_READY_WAITING_DAYS = payoutReadyWaitingDays;
    }
}
