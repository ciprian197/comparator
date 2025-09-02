package com.transact.comparator.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ComparisonResultDto(FileResult firstFileResult, FileResult secondFileResult) {

    @Builder
    public record FileResult(String fileName, int totalRecords, int matchedRecords, int unmatchedRecords,
                             List<TransactionDto> unmatchedTransactions) {
    }

    @Builder
    public record TransactionDto(String id, LocalDateTime date, String reference, BigDecimal amount,
                                 String potentialMatchId) {
    }

}

