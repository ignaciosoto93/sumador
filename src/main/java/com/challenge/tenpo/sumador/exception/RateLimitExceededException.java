package com.challenge.tenpo.sumador.exception;

public class RateLimitExceededException extends RuntimeException {

    private String debugMessage;

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitExceededException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
    }

    public RateLimitExceededException(String message, Throwable cause, String debugMessage) {
        super(message, cause);
        this.debugMessage = debugMessage;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

}
