package com.transact.comparator.service.similarity;

import java.time.Duration;
import java.time.LocalDateTime;

public class LocalDateTimeSimilarityCalculator implements SimilarityCalculator<LocalDateTime> {

    private static final long TIME_TOLERANCE_MINUTES = 2;

    @Override
    public double calculateSimilarity(LocalDateTime firstValue, LocalDateTime secondValue) {
        if (firstValue == null && secondValue == null) {
            return 1.0;
        }
        if (firstValue == null || secondValue == null) {
            return 0.0;
        }

        Duration difference = Duration.between(firstValue, secondValue).abs();
        long differenceInMinutes = difference.toMinutes();

        if (differenceInMinutes <= TIME_TOLERANCE_MINUTES) {
            return 1.0;
        }

        return 0.0;
    }
}
