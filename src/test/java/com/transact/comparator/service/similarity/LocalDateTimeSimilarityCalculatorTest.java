package com.transact.comparator.service.similarity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeSimilarityCalculatorTest {

    private final LocalDateTimeSimilarityCalculator calculator = new LocalDateTimeSimilarityCalculator();

    @Test
    public void calculateSimilarity_BothValuesAreNull_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity(null, null);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_FirstValueIsNull_ShouldReturnZero() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // When
        double similarity = calculator.calculateSimilarity(null, dateTime);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_SecondValueIsNull_ShouldReturnZero() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // When
        double similarity = calculator.calculateSimilarity(dateTime, null);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_BothValuesAreEqual_ShouldReturnOne() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // When
        double similarity = calculator.calculateSimilarity(dateTime, dateTime);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_ValuesWithinTolerance_ShouldReturnOne() {
        // Given - within 2 minutes tolerance
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 15, 10, 31, 30);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_ValuesAtExactTolerance_ShouldReturnOne() {
        // Given - exactly 2 minutes difference
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 15, 10, 32, 0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_ValuesJustOutsideTolerance_ShouldReturnZero() {
        // Given
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 15, 10, 33, 0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0);
    }

    @Test
    public void calculateSimilarity_TwentyFourHourDifference_ShouldReturnZero() {
        // Given
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 16, 10, 30, 0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(0.0);
    }

    @Test
    public void calculateSimilarity_SecondValueBeforeFirst_ShouldCalculateCorrectly() {
        // Given
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 15, 10, 29, 0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_DifferentDates_ShouldCalculateCorrectly() {
        // Given
        LocalDateTime firstValue = LocalDateTime.of(2024, 1, 15, 23, 59, 59);
        LocalDateTime secondValue = LocalDateTime.of(2024, 1, 16, 0, 1, 0);

        // When
        double similarity = calculator.calculateSimilarity(firstValue, secondValue);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

}
