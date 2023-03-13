package com.challenge.tenpo.sumador.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(value = {ExternalServiceException.class})
    protected ResponseEntity<Object> handleExternalServiceException(ExternalServiceException ex) {
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex.getDebugMessage());
        LOGGER.error("ExternalServiceException: {}", ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {RateLimitExceededException.class})
    protected ResponseEntity<Object> handleRateLimitExceededException(RateLimitExceededException ex) {
        ApiError apiError = new ApiError(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(),ex.getDebugMessage());
        LOGGER.error("RateLimitExceededException: {}", ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getLocalizedMessage());
        LOGGER.error("Exception: {}", ex.getMessage(), ex);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}





