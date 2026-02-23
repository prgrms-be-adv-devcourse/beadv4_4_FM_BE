package com.mossy.benchmark;

import com.mossy.boundedContext.payout.domain.calculator.CarbonCalculator;
import com.mossy.boundedContext.payout.domain.calculator.DonationCalculator;
import com.mossy.boundedContext.payout.domain.calculator.FeeCalculator;
import com.mossy.boundedContext.payout.domain.calculator.WeightCalculator;
import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import com.mossy.shared.payout.enums.CarbonGrade;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * [JMH Benchmark] 정산 계산기 성능 벤치마크
 *
 * 실행 방법:
 *   ./gradlew :payout:jmh
 *
 * 결과 파일:
 *   payout/build/reports/jmh/results.txt
 *
 * 측정 항목:
 *   - Throughput(thrpt): 초당 처리 건수 (ops/ms)
 *   - AverageTime(avgt): 1건당 평균 처리 시간 (ms)
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CalculatorBenchmark {

    private WeightCalculator weightCalculator;
    private CarbonCalculator carbonCalculator;
    private FeeCalculator feeCalculator;
    private DonationCalculator donationCalculator;

    // 시나리오별 DTO — 무게/거리/금액이 다른 3가지 케이스
    private PayoutCandidateCreateDto dtoSmallNear;      // 소형 + 근거리 (탄소 최소)
    private PayoutCandidateCreateDto dtoLargeIsland;    // 대형 + 도서제주 (탄소 최대)
    private PayoutCandidateCreateDto dtoTypical;        // 중형 + 중거리 (일반 케이스)

    @Setup(Level.Trial)
    public void setUp() {
        // @Value 대신 setter로 static 필드 직접 주입
        carbonCalculator = new CarbonCalculator();
        carbonCalculator.setCarbonCoefficient(new BigDecimal("0.01"));

        feeCalculator = new FeeCalculator(carbonCalculator);

        donationCalculator = new DonationCalculator(carbonCalculator, feeCalculator);
        donationCalculator.setMaxDonationRate(new BigDecimal("0.50"));

        weightCalculator = new WeightCalculator();

        // 시나리오 1: 소형 상품, 근거리 배송, 소액
        dtoSmallNear = PayoutCandidateCreateDto.builder()
                .orderPrice(new BigDecimal("5000"))
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("25"))
                .paymentDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        // 시나리오 2: 대형 상품, 도서제주 배송, 고액
        dtoLargeIsland = PayoutCandidateCreateDto.builder()
                .orderPrice(new BigDecimal("500000"))
                .weightGrade("대형")
                .deliveryDistance(new BigDecimal("400"))
                .paymentDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        // 시나리오 3: 중형 상품, 중거리 배송, 일반 금액
        dtoTypical = PayoutCandidateCreateDto.builder()
                .orderPrice(new BigDecimal("50000"))
                .weightGrade("중형")
                .deliveryDistance(new BigDecimal("200"))
                .paymentDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();
    }

    // =========================================================
    // WeightCalculator — 경계값 비교 (단순 BigDecimal compare)
    // =========================================================

    @Benchmark
    public String weightCalc_small() {
        return weightCalculator.determineWeightGrade(new BigDecimal("0.5"));
    }

    @Benchmark
    public String weightCalc_medium() {
        return weightCalculator.determineWeightGrade(new BigDecimal("7.5"));
    }

    @Benchmark
    public String weightCalc_large() {
        return weightCalculator.determineWeightGrade(new BigDecimal("15"));
    }

    // =========================================================
    // CarbonGrade.fromCarbon() — enum 선형 탐색
    // =========================================================

    @Benchmark
    public CarbonGrade carbonGrade_grade1() {
        // 탐색 첫 번째에서 바로 매칭 (최선)
        return CarbonGrade.fromCarbon(new BigDecimal("0.25"));
    }

    @Benchmark
    public CarbonGrade carbonGrade_grade10() {
        // 탐색 마지막에서 매칭 (최악)
        return CarbonGrade.fromCarbon(new BigDecimal("60"));
    }

    // =========================================================
    // CarbonCalculator — 무게 switch + BigDecimal 곱셈
    // =========================================================

    @Benchmark
    public BigDecimal carbonCalc_smallNear() {
        return carbonCalculator.calculate(dtoSmallNear);
    }

    @Benchmark
    public BigDecimal carbonCalc_largeIsland() {
        return carbonCalculator.calculate(dtoLargeIsland);
    }

    // =========================================================
    // FeeCalculator — 단순 20% 곱셈
    // =========================================================

    @Benchmark
    public BigDecimal feeCalc_small() {
        return feeCalculator.calculate(dtoSmallNear);
    }

    @Benchmark
    public BigDecimal feeCalc_large() {
        return feeCalculator.calculate(dtoLargeIsland);
    }

    @Benchmark
    public BigDecimal feeCalc_typical() {
        return feeCalculator.calculate(dtoTypical);
    }

    // =========================================================
    // DonationCalculator — 전체 체인 (fee + carbon + grade + 기부금)
    // =========================================================

    @Benchmark
    public BigDecimal donationCalc_smallNear() {
        // GRADE_1 → fee × 5% (기부율 최저)
        return donationCalculator.calculate(dtoSmallNear);
    }

    @Benchmark
    public BigDecimal donationCalc_largeIsland() {
        // GRADE_10 → fee × 50% (기부율 최고)
        return donationCalculator.calculate(dtoLargeIsland);
    }

    @Benchmark
    public BigDecimal donationCalc_typical() {
        // GRADE_5~6 범위 (일반 케이스)
        return donationCalculator.calculate(dtoTypical);
    }

    // =========================================================
    // 풀 파이프라인 — 실제 주문 처리 전체 흐름 시뮬레이션
    // (weightGrade 결정 → carbon 계산 → fee 계산 → donation 계산)
    // =========================================================

    @Benchmark
    public BigDecimal fullPipeline_typical() {
        // 1. 무게로 등급 결정
        String weightGrade = weightCalculator.determineWeightGrade(new BigDecimal("7.5"));

        // 2. 계산용 DTO 생성
        PayoutCandidateCreateDto dto = PayoutCandidateCreateDto.builder()
                .orderPrice(new BigDecimal("50000"))
                .weightGrade(weightGrade)
                .deliveryDistance(new BigDecimal("200"))
                .paymentDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        // 3. 수수료 + 기부금 계산
        feeCalculator.calculate(dto);
        return donationCalculator.calculate(dto);
    }
}
