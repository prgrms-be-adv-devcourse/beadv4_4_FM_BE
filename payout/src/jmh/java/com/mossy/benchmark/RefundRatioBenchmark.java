package com.mossy.benchmark;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * [JMH Benchmark] 환불 비율 배분 알고리즘 성능 벤치마크
 *
 * PayoutRefundUseCase.refundCandidates() 내부의 핵심 로직:
 * 후보 아이템 N건에 환불 금액을 비율로 배분 + 잔여금 보정
 *
 * 실제 운영에서 하나의 주문에 후보가 3~5건이므로
 * 아이템 수(1, 3, 5, 10, 50)별로 처리 시간 차이를 측정
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class RefundRatioBenchmark {

    // @Param으로 후보 아이템 수를 파라미터화하여 N별 성능 비교
    @Param({"1", "3", "5", "10", "50"})
    private int candidateCount;

    private List<BigDecimal> amounts;
    private BigDecimal refundAmount;
    private BigDecimal totalAmount;

    @Setup(Level.Trial)
    public void setUp() {
        // 후보 아이템 금액 리스트 구성
        // 실제와 유사하게: 대금 6, 수수료 2, 기부금 1, 나머지 1 비율
        amounts = new ArrayList<>();
        for (int i = 0; i < candidateCount; i++) {
            // 아이템마다 다른 금액 부여 (1000 ~ 1000+i*100)
            amounts.add(new BigDecimal(1000 + i * 100));
        }

        totalAmount = amounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 전체 금액의 절반 환불
        refundAmount = totalAmount.divide(BigDecimal.TWO, 0, RoundingMode.HALF_UP);
    }

    /**
     * 핵심 비율 배분 알고리즘 벤치마크
     * PayoutRefundUseCase.refundCandidates()와 동일한 로직
     */
    @Benchmark
    public List<BigDecimal> refundAllocation() {
        List<BigDecimal> allocatedAmounts = new ArrayList<>();
        BigDecimal allocatedSum = BigDecimal.ZERO;

        // 1단계: 각 아이템에 비율 × 환불금액 (내림 처리)
        for (BigDecimal amount : amounts) {
            BigDecimal ratio = amount.divide(totalAmount, 12, RoundingMode.HALF_UP);
            BigDecimal itemRefund = refundAmount.multiply(ratio).setScale(0, RoundingMode.DOWN);
            allocatedAmounts.add(itemRefund);
            allocatedSum = allocatedSum.add(itemRefund);
        }

        // 2단계: 잔여금 보정 — 마지막 아이템에 합산
        BigDecimal remainder = refundAmount.subtract(allocatedSum);
        if (!allocatedAmounts.isEmpty() && remainder.compareTo(BigDecimal.ZERO) != 0) {
            int last = allocatedAmounts.size() - 1;
            allocatedAmounts.set(last, allocatedAmounts.get(last).add(remainder));
        }

        return allocatedAmounts;
    }

    /**
     * totalAmount 계산만 단독 측정
     * stream().reduce() 비용 파악
     */
    @Benchmark
    public BigDecimal totalAmountCalculation() {
        return amounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 비율 계산만 단독 측정 (divide + multiply)
     */
    @Benchmark
    public BigDecimal singleRatioCalculation() {
        BigDecimal ratio = amounts.get(0).divide(totalAmount, 12, RoundingMode.HALF_UP);
        return refundAmount.multiply(ratio).setScale(0, RoundingMode.DOWN);
    }
}
