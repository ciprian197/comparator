package com.transact.comparator.service;

import com.transact.comparator.domain.Transaction;
import com.transact.comparator.service.similarity.BigDecimalSimilarityCalculator;
import com.transact.comparator.service.similarity.LocalDateTimeSimilarityCalculator;
import com.transact.comparator.service.similarity.SimilarityCalculator;
import com.transact.comparator.service.similarity.StringSimilarityCalculator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class PotentialMatchCalculator {

    private static final double MATCH_THRESHOLD = 0.5;

    private static final List<FieldMatchCalculator<?>> fieldCalculators = List.of(
            new FieldMatchCalculator<>(Transaction::getId, new StringSimilarityCalculator(), 0.5),
            new FieldMatchCalculator<>(Transaction::getDate, new LocalDateTimeSimilarityCalculator(), 0.3),
            new FieldMatchCalculator<>(Transaction::getAmount, new BigDecimalSimilarityCalculator(), 0.2)
    );

    public static boolean arePotentialMatch(final Transaction firstTransaction, final Transaction secondTransaction) {
        if (firstTransaction == null || secondTransaction == null) {
            log.warn("One or both transactions are null: firstTransaction={}, secondTransaction={}", firstTransaction, secondTransaction);
            return false;
        }

        final double matchScore = fieldCalculators.stream()
                .map(fieldMatchCalculator -> fieldMatchCalculator.calculateMatchScore(firstTransaction, secondTransaction))
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        log.debug("Match score for transaction {} and transaction {} is {}", firstTransaction.getId(), secondTransaction.getId(), matchScore);

        return matchScore >= MATCH_THRESHOLD;
    }

    private record FieldMatchCalculator<T>(Function<Transaction, T> valueSupplier,
                                           SimilarityCalculator<T> calculator,
                                           double weight) {

        public double calculateMatchScore(Transaction t1, Transaction t2) {
            T value1 = valueSupplier.apply(t1);
            T value2 = valueSupplier.apply(t2);
            double similarity = calculator.calculateSimilarity(value1, value2);
            return similarity * weight;
        }
    }

}
