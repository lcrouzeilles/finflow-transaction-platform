package com.finflow.txp.common.exception;

public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException(String message) {
        super(message);
    }
}
