package com.transact.comparator.exception;

public class InvalidDataException extends ComparatorServiceException {
    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
