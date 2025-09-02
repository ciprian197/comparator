package com.transact.comparator.service;

import com.transact.comparator.domain.Transaction;
import com.transact.comparator.exception.InvalidDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Set;

import static com.transact.comparator.utils.FileUtils.createMockFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CsvParsingServiceTest {

    private CsvParsingService csvParsingService;

    @BeforeEach
    void setUp() {
        csvParsingService = new CsvParsingService();
    }

    @Test
    public void parseTransactions_ValidCsv_ShouldReturnTransactions() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,0584011808649511,1,P_WALLET_REF
                """;
        MultipartFile file = createMockFile(csvContent);

        // When
        Set<Transaction> transactions = csvParsingService.parseTransactions(file);

        // Then
        assertThat(transactions).hasSize(1);
        Transaction transaction = transactions.iterator().next();
        assertThat(transaction.getId()).isEqualTo("0584011808649511");
        assertThat(transaction.getProfileName()).isEqualTo("Card Campaign");
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.valueOf(-20000));
    }

    @Test
    public void parseTransactions_EmptyFile_ShouldReturnEmptySet() {
        // Given
        MultipartFile file = createMockFile("");

        // When
        Set<Transaction> result = csvParsingService.parseTransactions(file);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void parseTransactions_NullTransactionId_ShouldFilterOut() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,,1,P_WALLET_REF
                Card Campaign,2014-01-11 22:27:44,-10000,*MOLEPS ATM25,DEDUCT,TXN123,1,P_WALLET_REF2
                """;
        MultipartFile file = createMockFile(csvContent);

        // When
        Set<Transaction> transactions = csvParsingService.parseTransactions(file);

        // Then
        assertThat(transactions).hasSize(1);
        assertThat(transactions.iterator().next().getId()).isEqualTo("TXN123");
    }

    @Test
    public void parseTransactions_InvalidAmountFormat_ShouldThrowException() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,invalid-amount,*MOLEPS ATM25,DEDUCT,TXN123,1,P_WALLET_REF
                """;
        MultipartFile file = createMockFile(csvContent);

        // When & Then
        assertThatThrownBy(() -> csvParsingService.parseTransactions(file))
                .isInstanceOf(InvalidDataException.class);
    }

    @Test
    public void parseTransactions_MultipleTransactions_ShouldReturnAll() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,TXN001,1,P_WALLET_1
                Card Campaign,2014-01-11 22:39:11,-10000,*MOGODITSHANE2,DEDUCT,TXN002,1,P_WALLET_2
                Card Campaign,2014-01-11 23:28:11,-5000,CAPITAL BANK,DEDUCT,TXN003,1,P_WALLET_3
                """;
        MultipartFile file = createMockFile(csvContent);

        // When
        Set<Transaction> transactions = csvParsingService.parseTransactions(file);

        // Then
        assertThat(transactions).hasSize(3);
        assertThat(transactions)
                .extracting(Transaction::getId)
                .containsExactlyInAnyOrder("TXN001", "TXN002", "TXN003");
    }

    @Test
    public void parseTransactions_FileWithMissingHeader_ShouldReturnTransactions() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,0584011808649511,1,P_WALLET_REF
                """;
        MultipartFile file = createMockFile(csvContent);

        // When
        assertThatThrownBy(() -> csvParsingService.parseTransactions(file))
                // Then
                .isInstanceOf(InvalidDataException.class);
    }

    @Test
    public void parseTransactions_QuotedFieldsWithCommas_ShouldParseCorrectly() {
        // Given
        String csvContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                "Card Campaign, Special",2014-01-11 22:27:44,-20000,"*MOLEPS ATM25, MOLEPOLOLE, BW",DEDUCT,TXN123,1,P_WALLET_REF
                """;
        MultipartFile file = createMockFile(csvContent);

        // When
        Set<Transaction> transactions = csvParsingService.parseTransactions(file);

        // Then
        assertThat(transactions).hasSize(1);
        Transaction transaction = transactions.iterator().next();
        assertThat(transaction.getProfileName()).isEqualTo("Card Campaign, Special");
        assertThat(transaction.getNarrative()).isEqualTo("*MOLEPS ATM25, MOLEPOLOLE, BW");
    }

    @Test
    public void parseTransactions_NullFile_ShouldReturnEmptySet() {
        // When
        Set<Transaction> result = csvParsingService.parseTransactions(null);

        // Then
        assertThat(result).isEmpty();
    }

}
