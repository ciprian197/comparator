package com.transact.comparator.service;

import com.transact.comparator.domain.Transaction;
import com.transact.comparator.dto.ComparisonResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComparisonServiceTest {

    @Mock
    private CsvParsingService csvParsingService;

    @InjectMocks
    private ComparisonService comparisonService;

    @Test
    public void compare_BothTransactionsSetsAreEmpty_DefaultEmptyResult() {
        // Given
        MultipartFile firstFile = mock(MultipartFile.class);
        MultipartFile secondFile = mock(MultipartFile.class);

        when(csvParsingService.parseTransactions(firstFile)).thenReturn(Collections.emptySet());
        when(csvParsingService.parseTransactions(secondFile)).thenReturn(Collections.emptySet());

        when(firstFile.getOriginalFilename()).thenReturn("first.csv");
        when(secondFile.getOriginalFilename()).thenReturn("second.csv");

        // When
        final ComparisonResultDto result = comparisonService.compare(firstFile, secondFile);

        // Then
        assertThat(result.firstFileResult().fileName()).isEqualTo("first.csv");
        assertThat(result.firstFileResult().totalRecords()).isEqualTo(0);
        assertThat(result.firstFileResult().matchedRecords()).isEqualTo(0);
        assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(0);

        assertThat(result.secondFileResult().fileName()).isEqualTo("second.csv");
        assertThat(result.secondFileResult().totalRecords()).isEqualTo(0);
        assertThat(result.secondFileResult().matchedRecords()).isEqualTo(0);
        assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(0);
    }

    @Test
    public void compare_OneSetHasValuesWhileTheOtherIsEmpty_ResultWithOneFileUnmatched() {
        // Given
        MultipartFile firstFile = mock(MultipartFile.class);
        MultipartFile secondFile = mock(MultipartFile.class);

        final Set<Transaction> transactions = Set.of(
                Transaction.builder().id("TXN001")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN002")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build()
        );

        when(csvParsingService.parseTransactions(firstFile)).thenReturn(new HashSet<>());
        when(csvParsingService.parseTransactions(secondFile)).thenReturn(new HashSet<>(transactions));

        when(firstFile.getOriginalFilename()).thenReturn("first.csv");
        when(secondFile.getOriginalFilename()).thenReturn("second.csv");

        // When
        final ComparisonResultDto result = comparisonService.compare(firstFile, secondFile);

        // Then
        assertThat(result.firstFileResult().fileName()).isEqualTo("first.csv");
        assertThat(result.firstFileResult().totalRecords()).isEqualTo(0);
        assertThat(result.firstFileResult().matchedRecords()).isEqualTo(0);
        assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(0);

        assertThat(result.secondFileResult().fileName()).isEqualTo("second.csv");
        assertThat(result.secondFileResult().totalRecords()).isEqualTo(2);
        assertThat(result.secondFileResult().matchedRecords()).isEqualTo(0);
        assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(2);

        List<ComparisonResultDto.TransactionDto> unmatchedTransactions = result.secondFileResult().unmatchedTransactions();
        assertThat(unmatchedTransactions).hasSize(2);
        assertThat(unmatchedTransactions).map(ComparisonResultDto.TransactionDto::id).contains("TXN001", "TXN002");
    }

    @Test
    public void compare_BothFilesHaveTheSameContent_ResultWithAllMatching() {
        // Given
        MultipartFile firstFile = mock(MultipartFile.class);
        MultipartFile secondFile = mock(MultipartFile.class);

        final Set<Transaction> transactions = Set.of(
                Transaction.builder().id("TXN001")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN002")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build()
        );

        when(csvParsingService.parseTransactions(firstFile)).thenReturn(new HashSet<>(transactions));
        when(csvParsingService.parseTransactions(secondFile)).thenReturn(new HashSet<>(transactions));

        when(firstFile.getOriginalFilename()).thenReturn("first.csv");
        when(secondFile.getOriginalFilename()).thenReturn("second.csv");

        // When
        final ComparisonResultDto result = comparisonService.compare(firstFile, secondFile);

        // Then
        assertThat(result.firstFileResult().fileName()).isEqualTo("first.csv");
        assertThat(result.firstFileResult().totalRecords()).isEqualTo(2);
        assertThat(result.firstFileResult().matchedRecords()).isEqualTo(2);
        assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(0);

        assertThat(result.secondFileResult().fileName()).isEqualTo("second.csv");
        assertThat(result.secondFileResult().totalRecords()).isEqualTo(2);
        assertThat(result.secondFileResult().matchedRecords()).isEqualTo(2);
        assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(0);
    }

    @Test
    public void compare_SameContentButOneFileHasOneAdditionalEntry_DefaultEmptyResult() {
        // Given
        MultipartFile firstFile = mock(MultipartFile.class);
        MultipartFile secondFile = mock(MultipartFile.class);

        final Set<Transaction> firstFileTransactions = Set.of(
                Transaction.builder().id("TXN001")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN002")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build()
        );

        final Set<Transaction> secondFileTransactions = new HashSet<>(firstFileTransactions);
        secondFileTransactions.add(Transaction.builder().id("TXN003")
                .amount(BigDecimal.TEN)
                .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                .walletReference("reference").build());

        when(csvParsingService.parseTransactions(firstFile)).thenReturn(new HashSet<>(firstFileTransactions));
        when(csvParsingService.parseTransactions(secondFile)).thenReturn(new HashSet<>(secondFileTransactions));

        when(firstFile.getOriginalFilename()).thenReturn("first.csv");
        when(secondFile.getOriginalFilename()).thenReturn("second.csv");

        // When
        final ComparisonResultDto result = comparisonService.compare(firstFile, secondFile);

        // Then
        assertThat(result.firstFileResult().fileName()).isEqualTo("first.csv");
        assertThat(result.firstFileResult().totalRecords()).isEqualTo(2);
        assertThat(result.firstFileResult().matchedRecords()).isEqualTo(2);
        assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(0);

        assertThat(result.secondFileResult().fileName()).isEqualTo("second.csv");
        assertThat(result.secondFileResult().totalRecords()).isEqualTo(3);
        assertThat(result.secondFileResult().matchedRecords()).isEqualTo(2);
        assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(1);

        List<ComparisonResultDto.TransactionDto> unmatchedTransactions = result.secondFileResult().unmatchedTransactions();
        assertThat(unmatchedTransactions).hasSize(1);
        assertThat(unmatchedTransactions).map(ComparisonResultDto.TransactionDto::id).containsExactly("TXN003");
    }

    @Test
    public void compare_DifferentContentsButWithMatchingAndPotentialMatchingEntries_DefaultEmptyResult() {
        // Given
        MultipartFile firstFile = mock(MultipartFile.class);
        MultipartFile secondFile = mock(MultipartFile.class);

        final Set<Transaction> firstFileTransactions = Set.of(
                Transaction.builder().id("TXN001")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 15, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN002")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 15, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN004")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 1, 1))
                        .walletReference("reference").build()
        );

        final Set<Transaction> secondFileTransactions = Set.of(
                Transaction.builder().id("TXN001")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 15, 1, 1))
                        .walletReference("reference").build(),
                Transaction.builder().id("TXN003")
                        .amount(BigDecimal.TEN)
                        .date(LocalDateTime.of(2025, 9, 9, 1, 0, 1))
                        .walletReference("reference").build());

        when(csvParsingService.parseTransactions(firstFile)).thenReturn(new HashSet<>(firstFileTransactions));
        when(csvParsingService.parseTransactions(secondFile)).thenReturn(new HashSet<>(secondFileTransactions));

        when(firstFile.getOriginalFilename()).thenReturn("first.csv");
        when(secondFile.getOriginalFilename()).thenReturn("second.csv");

        // When
        final ComparisonResultDto result = comparisonService.compare(firstFile, secondFile);

        // Then
        assertThat(result.firstFileResult().fileName()).isEqualTo("first.csv");
        assertThat(result.firstFileResult().totalRecords()).isEqualTo(3);
        assertThat(result.firstFileResult().matchedRecords()).isEqualTo(1);
        assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(2);

        assertThat(result.secondFileResult().fileName()).isEqualTo("second.csv");
        assertThat(result.secondFileResult().totalRecords()).isEqualTo(2);
        assertThat(result.secondFileResult().matchedRecords()).isEqualTo(1);
        assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(1);

        List<ComparisonResultDto.TransactionDto> firstUnmatchedTransactions = result.firstFileResult().unmatchedTransactions();
        List<ComparisonResultDto.TransactionDto> secondUnmatchedTransactions = result.secondFileResult().unmatchedTransactions();

        assertThat(firstUnmatchedTransactions).hasSize(2);
        assertThat(firstUnmatchedTransactions).map(ComparisonResultDto.TransactionDto::id).contains("TXN002", "TXN004");

        assertThat(firstUnmatchedTransactions.stream()
                .filter(transaction -> transaction.id().equals("TXN004"))
                .map(ComparisonResultDto.TransactionDto::potentialMatchId)
                .findFirst().get()).isEqualTo("TXN003");

        assertThat(secondUnmatchedTransactions).hasSize(1);
        assertThat(secondUnmatchedTransactions).map(ComparisonResultDto.TransactionDto::id).contains("TXN003");
        assertThat(secondUnmatchedTransactions.get(0).potentialMatchId()).isEqualTo("TXN004");
    }

}
