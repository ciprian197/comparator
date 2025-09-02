package com.transact.comparator.service;

import com.transact.comparator.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PotentialMatchCalculatorTest {

    @Test
    public void arePotentialMatch_IdenticalTransactions_ShouldReturnTrue() {
        // Given
        Transaction transaction = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction, transaction);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_ExactlyAtThreshold_ShouldReturnTrue() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            "TXN001",
            LocalDateTime.of(2024, 1, 16, 10, 30, 0),
            BigDecimal.valueOf(200.00)
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_JustAboveThreshold_ShouldReturnTrue() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            "TXN001",
            LocalDateTime.of(2024, 1, 15, 10, 30, 35),
            BigDecimal.valueOf(200.00)
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_CompletelyDifferentTransactions_ShouldReturnFalse() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            "TXN999",
            LocalDateTime.of(2024, 1, 16, 10, 30, 0),
            BigDecimal.valueOf(200.00)
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void arePotentialMatch_SameIdAndAmount_DifferentDate_ShouldReturnTrue() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            transaction1.getId(),
            LocalDateTime.of(2024, 1, 16, 10, 30, 0),
           transaction1.getAmount()
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_SameDateAndAmount_DifferentId_ShouldReturnTrue() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            "TXN999",
            transaction1.getDate(),
            transaction1.getAmount()
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_SimilarAmountAndDateWithinTolerance_ShouldReturnTrue() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransaction(
            "TXN0021",
            LocalDateTime.of(2024, 1, 15, 10, 31, 0),
            transaction1.getAmount()
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_NullValues_ShouldHandleGracefully() {
        // Given
        Transaction transaction1 = createTransactionWithNulls(null, null, null);
        Transaction transaction2 = createTransactionWithNulls(null, null, null);

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void arePotentialMatch_OneTransactionWithNulls_ShouldHandleGracefully() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransactionWithNulls(null, null, null);

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void arePotentialMatch_PartialNullValues_ShouldCalculateBasedOnNonNullFields() {
        // Given
        Transaction transaction1 = createTransaction(
            "TXN001", 
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            BigDecimal.valueOf(100.00)
        );
        
        Transaction transaction2 = createTransactionWithNulls(
            "TXN001",
            null,
            BigDecimal.valueOf(100.00)
        );

        // When
        boolean result = PotentialMatchCalculator.arePotentialMatch(transaction1, transaction2);

        // Then
        assertThat(result).isTrue();
    }

    private Transaction createTransaction(String id, LocalDateTime date, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setProfileName("Test Profile");
        transaction.setNarrative("Test Narrative");
        transaction.setDescription("Test Description");
        transaction.setType(1);
        transaction.setWalletReference("Test Wallet");
        return transaction;
    }

    private Transaction createTransactionWithNulls(String id, LocalDateTime date, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setProfileName("Test Profile");
        transaction.setNarrative("Test Narrative");
        transaction.setDescription("Test Description");
        transaction.setType(1);
        transaction.setWalletReference("Test Wallet");
        return transaction;
    }
}
