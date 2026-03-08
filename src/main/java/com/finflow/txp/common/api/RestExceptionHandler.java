package com.finflow.txp.common.api;

import com.finflow.txp.common.exception.BusinessRuleException;
import com.finflow.txp.common.exception.DuplicateRequestException;
import com.finflow.txp.common.exception.ResourceNotFoundException;
import com.finflow.txp.config.TracingSupport;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    private final TracingSupport tracingSupport;

    public RestExceptionHandler(TracingSupport tracingSupport) {
        this.tracingSupport = tracingSupport;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateRequestException ex) {
        return build(HttpStatus.CONFLICT, "IDEMPOTENT_REPLAY_CONFLICT", ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessRuleException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", ex.getMessage(), null);
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleForbidden(Exception ex) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Access denied", null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (ex instanceof MethodArgumentNotValidException manve) {
            for (FieldError fieldError : manve.getBindingResult().getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        } else {
            errors.put("request", ex.getMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error", null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                code,
                message,
                Instant.now(),
                tracingSupport.currentTraceId(),
                fieldErrors
        ));
    }
}
