package com.vouched.error;

// Ignored from error handlers
public class SoftException extends RuntimeException {
    public SoftException(String message) {
        super(message);
    }

    public SoftException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoftException(Throwable cause) {
        super(cause);
    }

    public SoftException() {

    }
}
