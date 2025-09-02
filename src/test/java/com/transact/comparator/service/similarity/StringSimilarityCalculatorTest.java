package com.transact.comparator.service.similarity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSimilarityCalculatorTest {

    private final StringSimilarityCalculator calculator = new StringSimilarityCalculator();

    @Test
    public void calculateSimilarity_BothValuesAreNull_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity(null, null);

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_BothValuesAreEqual_ShouldReturnOne() {
        // When
        double similarity = calculator.calculateSimilarity("abc", "abc");

        // Then
        assertThat(similarity).isEqualTo(1.0);
    }

    @Test
    public void calculateSimilarity_BothValuesHaveSameCharactersButInDifferentOrder_ShouldReturnZero() {
        // When
        double similarity = calculator.calculateSimilarity("abc", "bca");

        // Then
        assertThat(similarity).isEqualTo(0);
    }

    @Test
    public void calculateSimilarity_ValuesAreDifferent_ShouldReturnZero() {
        // When
        double similarity = calculator.calculateSimilarity("abc", "asde");

        // Then
        assertThat(similarity).isEqualTo(0);
    }

    @Test
    public void calculateSimilarity_OneIsNullAndOneEmpty_ShouldReturnZero() {
        // When
        double similarity = calculator.calculateSimilarity(null, "");

        // Then
        assertThat(similarity).isEqualTo(0);
    }

}
