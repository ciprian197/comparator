package com.transact.comparator.exception;

public class ComparatorServiceException extends RuntimeException {

    public ComparatorServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComparatorServiceException(Throwable cause) {
        super(cause);
    }

    public ComparatorServiceException(String message) {
        super(message);
    }
}
