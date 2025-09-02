package com.transact.comparator.service.similarity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalSimilarityCalculatorTest {

    private final BigDecimalSimilarityCalculator calculator = new BigDecimalSimilarityCalculator();

    @Test
    public void calculateSimilarity_BothValuesAreNull_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity(null, null);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_FirstValueIsNull_ShouldReturnZero() {
        // When
        double similarity = calculator.calculateSimilarity(null, BigDecimal.valueOf(100.0));

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_SecondValueIsNull_ShouldReturnZero() {
        // When
        double similarity = calculator.calculateSimilarity(BigDecimal.valueOf(100.0), null);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_BothValuesAreEqual_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity(BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0));

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_BothValuesAreZero_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity(BigDecimal.ZERO, BigDecimal.ZERO);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_ValuesAtExactTolerance_ShouldReturnOne() {
        // Given
        BigDecimal firstValue = BigDecimal.valueOf(100.00);
        BigDecimal secondValue = BigDecimal.valueOf(100.01);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_ValuesWithSmallPercentageDifference_ShouldReturnHighSimilarity() {
        // Given
        BigDecimal firstValue = BigDecimal.valueOf(100.0);
        BigDecimal secondValue = BigDecimal.valueOf(101.0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0);
    }

    @Test
    public void calculateSimilarity_ValuesWithLargePercentageDifference_ShouldReturnLowSimilarity() {
        // Given
        BigDecimal firstValue = BigDecimal.valueOf(100.0);
        BigDecimal secondValue = BigDecimal.valueOf(150.0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }


    @Test
    public void calculateSimilarity_NegativeValues_ShouldCalculateCorrectly() {
        // Given
        BigDecimal firstValue = BigDecimal.valueOf(-100.0);
        BigDecimal secondValue = BigDecimal.valueOf(-110.0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_MixedSignValues_ShouldCalculateCorrectly() {
        // Given
        BigDecimal firstValue = BigDecimal.valueOf(100.0);
        BigDecimal secondValue = BigDecimal.valueOf(-100.0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_VerySmallValues_ShouldCalculateCorrectly() {
        BigDecimal firstValue = BigDecimal.valueOf(0.005);
        BigDecimal secondValue = BigDecimal.valueOf(0.008);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

}
