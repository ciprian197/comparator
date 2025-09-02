package com.transact.comparator.service.similarity;

import java.util.Objects;

public class StringSimilarityCalculator implements SimilarityCalculator<String> {

    @Override
    public double calculateSimilarity(String firstValue, String secondValue) {
        return Objects.equals(firstValue, secondValue) ? 1.0 : 0.0;
    }

}
