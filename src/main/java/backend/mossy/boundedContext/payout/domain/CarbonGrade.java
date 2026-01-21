package backend.mossy.boundedContext.payout.domain;

import java.math.BigDecimal;

/**
 * 탄소 배출량에 따른 등급
 */
public enum CarbonGrade {
    A(new BigDecimal("0"),   new BigDecimal("10"),  new BigDecimal("0.10")),  // 0~10kg: 10%
    B(new BigDecimal("10"),  new BigDecimal("30"),  new BigDecimal("0.20")),  // 10~30kg: 20%
    C(new BigDecimal("30"),  new BigDecimal("50"),  new BigDecimal("0.30")),  // 30~50kg: 30%
    D(new BigDecimal("50"),  new BigDecimal("99999"), new BigDecimal("0.40")); // 50kg~: 40%

    private final BigDecimal minCarbon;
    private final BigDecimal maxCarbon;
    private final BigDecimal donationRate;  // 수수료 대비 기부 비율

    CarbonGrade(BigDecimal minCarbon, BigDecimal maxCarbon, BigDecimal donationRate) {
        this.minCarbon = minCarbon;
        this.maxCarbon = maxCarbon;
        this.donationRate = donationRate;
    }

    public BigDecimal getDonationRate() {
        return donationRate;
    }

    public BigDecimal getMinCarbon() {
        return minCarbon;
    }

    public BigDecimal getMaxCarbon() {
        return maxCarbon;
    }

    /**
     * 탄소량으로 등급 판정
     */
    public static CarbonGrade fromCarbon(BigDecimal carbon) {
        for (CarbonGrade grade : values()) {
            if (carbon.compareTo(grade.minCarbon) >= 0 &&
                carbon.compareTo(grade.maxCarbon) < 0) {
                return grade;
            }
        }
        return D; // 기본값: 최고 등급
    }
}
