package com.challenge.tenpo.sumador.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExternalServiceException extends RuntimeException {

    private String debugMessage;

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
    }

    public ExternalServiceException(String message, Throwable cause, String debugMessage) {
        super(message, cause);
        this.debugMessage = debugMessage;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

}
