package com.transact.comparator.service;

import com.transact.comparator.domain.Transaction;
import com.transact.comparator.dto.ComparisonResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ComparisonService {

    private final CsvParsingService csvParsingService;

    public ComparisonResultDto compare(final MultipartFile firstFile, final MultipartFile secondFile) {
        final Set<Transaction> firstFileTransactions = csvParsingService.parseTransactions(firstFile);
        final Set<Transaction> secondFileTransactions = csvParsingService.parseTransactions(secondFile);

        if (CollectionUtils.isEmpty(firstFileTransactions) && CollectionUtils.isEmpty(secondFileTransactions)) {
            return getEmptyResponse(firstFile, secondFile);
        }

        final int firstFileTotalRecords = firstFileTransactions.size();
        final int secondFileTotalRecords = secondFileTransactions.size();

        final Set<Transaction> copyOfFirstFileTransactions = new HashSet<>(firstFileTransactions);

        copyOfFirstFileTransactions.retainAll(secondFileTransactions);

        firstFileTransactions.removeAll(copyOfFirstFileTransactions);
        secondFileTransactions.removeAll(copyOfFirstFileTransactions);

        final UnmatchedTransactionsResult unmatchedTransactionsResult = processUnmatchedResults(firstFileTransactions, secondFileTransactions);

        return ComparisonResultDto.builder()
                .firstFileResult(ComparisonResultDto.FileResult.builder()
                        .fileName(firstFile.getOriginalFilename())
                        .totalRecords(firstFileTotalRecords)
                        .matchedRecords(copyOfFirstFileTransactions.size())
                        .unmatchedRecords(unmatchedTransactionsResult.firstFileUnmatchedTransactions.size())
                        .unmatchedTransactions(unmatchedTransactionsResult.firstFileUnmatchedTransactions).build())
                .secondFileResult(ComparisonResultDto.FileResult.builder()
                        .fileName(secondFile.getOriginalFilename())
                        .totalRecords(secondFileTotalRecords)
                        .matchedRecords(copyOfFirstFileTransactions.size())
                        .unmatchedRecords(unmatchedTransactionsResult.secondFileUnmatchedTransactions.size())
                        .unmatchedTransactions(unmatchedTransactionsResult.secondFileUnmatchedTransactions).build())
                .build();
    }

    private UnmatchedTransactionsResult processUnmatchedResults(final Set<Transaction> firstTransactions,
                                                                final Set<Transaction> secondTransactions) {
        final List<ComparisonResultDto.TransactionDto> firstUnmatchedTransactions = new ArrayList<>();
        final List<ComparisonResultDto.TransactionDto> secondUnmatchedTransactions = new ArrayList<>();

        for (final Transaction transaction : firstTransactions) {
            Transaction potentialMatch = secondTransactions.stream()
                    .filter(secondTransaction -> PotentialMatchCalculator.arePotentialMatch(transaction, secondTransaction))
                    .findFirst()
                    .orElse(null);

            if (potentialMatch != null) {
                secondTransactions.remove(potentialMatch);
                firstUnmatchedTransactions.add(toTransactionDto(transaction, potentialMatch.getId()));
                secondUnmatchedTransactions.add(toTransactionDto(potentialMatch, transaction.getId()));
            } else {
                firstUnmatchedTransactions.add(toTransactionDto(transaction, null));
            }
        }

        secondUnmatchedTransactions.addAll(toTransactionsDto(secondTransactions));

        return new UnmatchedTransactionsResult(firstUnmatchedTransactions, secondUnmatchedTransactions);
    }

    private Collection<ComparisonResultDto.TransactionDto> toTransactionsDto(Set<Transaction> secondTransactions) {
        return secondTransactions.stream()
                .map(transaction -> toTransactionDto(transaction, null))
                .sorted(Comparator.comparing(ComparisonResultDto.TransactionDto::date))
                .toList();
    }

    private static ComparisonResultDto getEmptyResponse(MultipartFile firstFile, MultipartFile secondFile) {
        return ComparisonResultDto.builder()
                .firstFileResult(ComparisonResultDto.FileResult.builder()
                        .fileName(firstFile.getOriginalFilename())
                        .totalRecords(0)
                        .matchedRecords(0)
                        .unmatchedRecords(0)
                        .unmatchedTransactions(List.of()).build())
                .secondFileResult(ComparisonResultDto.FileResult.builder()
                        .fileName(secondFile.getOriginalFilename())
                        .totalRecords(0)
                        .matchedRecords(0)
                        .unmatchedRecords(0)
                        .unmatchedTransactions(List.of()).build())
                .build();
    }

    private static ComparisonResultDto.TransactionDto toTransactionDto(Transaction transaction, String potentialMapTransactionId) {
        return ComparisonResultDto.TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .reference(transaction.getWalletReference())
                .potentialMatchId(potentialMapTransactionId)
                .build();
    }

    private record UnmatchedTransactionsResult(List<ComparisonResultDto.TransactionDto> firstFileUnmatchedTransactions,
                                               List<ComparisonResultDto.TransactionDto> secondFileUnmatchedTransactions) {
    }

}
