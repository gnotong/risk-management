package com.notgabs.corp.exception;

import java.time.OffsetDateTime;

public class ErrorResponse {
    public String code;
    public String message;
    public OffsetDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }
}
