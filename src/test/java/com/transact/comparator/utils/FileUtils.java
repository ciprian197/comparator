package com.transact.comparator.utils;

import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static MockMultipartFile createMockFile(String content) {
        return new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

}
