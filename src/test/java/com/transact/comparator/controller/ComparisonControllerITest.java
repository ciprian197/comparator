package com.transact.comparator.controller;

import com.transact.comparator.dto.ComparisonResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComparisonControllerITest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void compareTransactionFiles_OneFileIsMissing_InvalidDataStatus() {
        // Given
        final String firstFileContent = "";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("firstFile", new ByteArrayResource(firstFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions1.csv")
                .contentType(MediaType.TEXT_PLAIN);

        // When
        webTestClient.post().uri("/api/v1/transactions/compare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                // Then
                .expectStatus().is4xxClientError();
    }

    @Test
    public void compareTransactionFiles_BothFilesAreEmpty_DefaultEmptyFile() {
        // Given
        final String firstFileContent = "";
        final String secondFileContent = "";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("firstFile", new ByteArrayResource(firstFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions1.csv")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("secondFile", new ByteArrayResource(secondFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions2.csv")
                .contentType(MediaType.TEXT_PLAIN);

        // When
        webTestClient.post().uri("/api/v1/transactions/compare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody(ComparisonResultDto.class)
                .value(response -> {
                    ;
                    assertThat(response.firstFileResult()).isNotNull();
                    assertThat(response.firstFileResult().totalRecords()).isEqualTo(0);
                    assertThat(response.firstFileResult().matchedRecords()).isEqualTo(0);
                    assertThat(response.firstFileResult().unmatchedRecords()).isEqualTo(0);
                    assertThat(response.firstFileResult().unmatchedTransactions()).isEmpty();

                    assertThat(response.secondFileResult()).isNotNull();
                    assertThat(response.secondFileResult().totalRecords()).isEqualTo(0);
                    assertThat(response.secondFileResult().matchedRecords()).isEqualTo(0);
                    assertThat(response.secondFileResult().unmatchedRecords()).isEqualTo(0);
                    assertThat(response.secondFileResult().unmatchedTransactions()).isEmpty();
                });
    }

    @Test
    public void compareTransactionFiles_OneFileIsMissingHeaderColumn_InvalidData() {
        // Given
        final String firstFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,,1,P_WALLET_REF
                Card Campaign,2014-01-11 22:27:44,-10000,*MOLEPS ATM25,DEDUCT,TXN123,1,P_WALLET_REF2
                """;
        final String secondFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,0584011808649511,1,P_WALLET_REF
                """;

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("firstFile", new ByteArrayResource(firstFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions1.csv")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("secondFile", new ByteArrayResource(secondFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions2.csv")
                .contentType(MediaType.TEXT_PLAIN);

        // When
        webTestClient.post().uri("/api/v1/transactions/compare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                // Then
                .expectStatus().is4xxClientError();
    }

    @Test
    public void compareTransactionFiles_OneFileHasInvalidDatePattern_InvalidData() {
        // Given
        final String firstFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,TXN001,1,P_WALLET_REF
                Card Campaign,2014-01-11 22:45:44,-10000,*MOLEPS ATM25,DEDUCT,TXN002,1,P_WALLET_REF2
                Card Campaign,2014-01-11 23:27:44,-15000,*MOLEPS ATM25,DEDUCT,TXN003,1,P_WALLET_REF2
                """;
        final String secondFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,TXN001,1,P_WALLET_REF
                Card Campaign,2014-01-11 22:46:44,-10000,*MOLEPS ATM25,DEDUCT,TXN004,1,P_WALLET_REF2
                Card Campaign,2014-01-11 10:46:44,-10000,*MOLEPS ATM25,DEDUCT,TXN003,1,P_WALLET_REF2
                Card Campaign,2014-01-13 22:46:44,-10000,*MOLEPS ATM25,DEDUCT,TXN005,1,P_WALLET_REF2
                """;

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("firstFile", new ByteArrayResource(firstFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions1.csv")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("secondFile", new ByteArrayResource(secondFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions2.csv")
                .contentType(MediaType.TEXT_PLAIN);

        // When
        webTestClient.post().uri("/api/v1/transactions/compare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectBody(ComparisonResultDto.class)
                .value(result -> {
                    assertThat(result.firstFileResult()).isNotNull();
                    assertThat(result.firstFileResult().totalRecords()).isEqualTo(3);
                    assertThat(result.firstFileResult().matchedRecords()).isEqualTo(1);
                    assertThat(result.firstFileResult().unmatchedRecords()).isEqualTo(2);
                    assertThat(result.firstFileResult().unmatchedTransactions()).hasSize(2);
                    assertThat(result.firstFileResult().unmatchedTransactions())
                            .extracting(ComparisonResultDto.TransactionDto::id)
                            .containsExactlyInAnyOrder("TXN002", "TXN003");

                    assertThat(result.secondFileResult()).isNotNull();
                    assertThat(result.secondFileResult().totalRecords()).isEqualTo(4);
                    assertThat(result.secondFileResult().matchedRecords()).isEqualTo(1);
                    assertThat(result.secondFileResult().unmatchedRecords()).isEqualTo(3);
                    assertThat(result.secondFileResult().unmatchedTransactions()).hasSize(3);
                    assertThat(result.secondFileResult().unmatchedTransactions())
                            .extracting(ComparisonResultDto.TransactionDto::id)
                            .containsExactlyInAnyOrder("TXN003", "TXN004", "TXN005");

                });
    }

    @Test
    public void compareTransactionFiles_ValidData_ExpectedResponse() {
        // Given
        final String firstFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,11-01-2015T22:27:44,-20000,*MOLEPS ATM25,DEDUCT,,1,P_WALLET_REF
                Card Campaign,11-01-2015T22:27:44,-10000,*MOLEPS ATM25,DEDUCT,TXN123,1,P_WALLET_REF2
                """;
        final String secondFileContent = """
                ProfileName,TransactionDate,TransactionAmount,TransactionNarrative,TransactionDescription,TransactionID,TransactionType,WalletReference
                Card Campaign,2014-01-11 22:27:44,-20000,*MOLEPS ATM25,DEDUCT,0584011808649511,1,P_WALLET_REF
                """;

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("firstFile", new ByteArrayResource(firstFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions1.csv")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("secondFile", new ByteArrayResource(secondFileContent.getBytes(StandardCharsets.UTF_8)))
                .filename("transactions2.csv")
                .contentType(MediaType.TEXT_PLAIN);

        // When
        webTestClient.post().uri("/api/v1/transactions/compare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                // Then
                .expectStatus().is4xxClientError();
    }


}
