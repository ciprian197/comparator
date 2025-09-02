package com.transact.comparator.service.similarity;

import java.math.BigDecimal;

public class BigDecimalSimilarityCalculator implements SimilarityCalculator<BigDecimal> {

    private static final double AMOUNT_TOLERANCE = 0.01;

    @Override
    public double calculateSimilarity(BigDecimal firstValue, BigDecimal secondValue) {
        if (firstValue == null && secondValue == null) {
            return 1.0;
        }

        if (firstValue == null || secondValue == null) {
            return 0.0;
        }

        if (firstValue.compareTo(secondValue) == 0) {
            return 1.0;
        }

        BigDecimal difference = firstValue.subtract(secondValue).abs();
        if (difference.compareTo(BigDecimal.valueOf(AMOUNT_TOLERANCE)) <= 0) {
            return 1.0;
        }

        return 0.0;
    }

}
