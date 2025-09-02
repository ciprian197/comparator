package com.transact.comparator.service.similarity;

public interface SimilarityCalculator<T> {

    double calculateSimilarity(T firstValue, T secondValue);

}
