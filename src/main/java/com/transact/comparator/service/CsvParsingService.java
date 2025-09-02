package com.transact.comparator.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.transact.comparator.domain.Transaction;
import com.transact.comparator.exception.InvalidDataException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class CsvParsingService {

    private final CsvMapper csvMapper;

    public CsvParsingService() {
        this.csvMapper = new CsvMapper().configure(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS, true);
        this.csvMapper.registerModule(new JavaTimeModule());
    }

    public Set<Transaction> parseTransactions(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new HashSet<>();
        }

        final Set<Transaction> transactions = new HashSet<>();
        final CsvSchema csvSchema = csvMapper.schemaWithHeader();

        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<Transaction> iterator = csvMapper
                    .readerFor(Transaction.class)
                    .with(csvSchema)
                    .readValues(inputStream);

            while (iterator.hasNext()) {
                Transaction transaction = iterator.next();
                if (transaction != null && transaction.isValid()) {
                    transactions.add(transaction);
                }
            }
        } catch (CsvReadException exception) {
            throw new InvalidDataException("Failed to parse CSV file: " + exception.getMessage(), exception);
        } catch (RuntimeJsonMappingException runtimeJsonMappingException) {
            throw new InvalidDataException("Invalid data format in CSV file: " + runtimeJsonMappingException.getMessage(), runtimeJsonMappingException);
        } catch (Exception exception) {
            throw new InvalidDataException("Could not parse CSV file: " + file.getOriginalFilename(), exception);
        }

        return transactions;
    }

}
