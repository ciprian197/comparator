package com.transact.comparator.controller;

import com.transact.comparator.dto.ComparisonResultDto;
import com.transact.comparator.service.ComparisonService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/transactions")
public class ComparisonController {

    private final ComparisonService comparisonService;

    @PostMapping(value = "/compare", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ComparisonResultDto compareTransactionFiles(@RequestParam("firstFile") @NotNull MultipartFile firstFile,
                                                       @RequestParam("secondFile") @NotNull MultipartFile secondFile) {
        log.info("Comparing files: {} and {}", firstFile.getOriginalFilename(), secondFile.getOriginalFilename());
        final ComparisonResultDto resultDto = comparisonService.compare(firstFile, secondFile);

        log.info("Successfully compared files: {} and {}", firstFile.getOriginalFilename(), secondFile.getOriginalFilename());
        return resultDto;
    }

}
