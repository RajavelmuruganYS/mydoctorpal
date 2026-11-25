package com.hospital.ehr.exception;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts every failure into one JSON shape the frontend can display:
 *
 * {
 *   "success": false,
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed.",
 *   "errors": { "fieldName": "field message" },   // validation only
 *   "timestamp": "..."
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String message,
            Map<String, String> fieldErrors
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            body.put("errors", fieldErrors);
        }
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.putIfAbsent(
                        fieldError.getField(),
                        fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Invalid value."
                )
        );

        String message = errors.isEmpty()
                ? "Validation failed."
                : errors.values().iterator().next();

        return build(HttpStatus.BAD_REQUEST, message, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(
            HttpMessageNotReadableException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed request body. Check the field formats (dates use yyyy-MM-dd, times use HH:mm).",
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Invalid value for '" + exception.getName() + "'.",
                null
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Required parameter '" + exception.getParameterName() + "' is missing.",
                null
        );
    }

    /*
     * Services throw IllegalArgumentException for business-rule
     * violations and "not found" conditions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : "Invalid request.";

        HttpStatus status = message.toLowerCase().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return build(status, message, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException exception
    ) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), null);
    }

    /*
     * Services throw SecurityException for ownership violations,
     * e.g. a patient trying to read another patient's record.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurity(
            SecurityException exception
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                exception.getMessage() != null
                        ? exception.getMessage()
                        : "You do not have permission to access this resource.",
                null
        );
    }

    /*
     * Must be handled explicitly: @PreAuthorize failures are thrown
     * as AccessDeniedException (a RuntimeException) and would
     * otherwise be swallowed by the RuntimeException handler below.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException exception
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action.",
                null
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException exception
    ) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required or your session has expired.",
                null
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(
            ObjectOptimisticLockingFailureException exception
    ) {
        return build(
                HttpStatus.CONFLICT,
                "This record was modified by another user. Reload it and try again.",
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException exception
    ) {
        log.warn("Data integrity violation: {}", exception.getMostSpecificCause().getMessage());
        return build(
                HttpStatus.CONFLICT,
                "The request conflicts with existing data or references a record that does not exist.",
                null
        );
    }

    /*
     * Deliberate `throw new RuntimeException("message")` calls in the
     * services carry a user-facing message; anything else is an
     * unexpected failure and is reported generically.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(
            RuntimeException exception
    ) {
        boolean deliberate = exception.getClass() == RuntimeException.class
                && exception.getMessage() != null;

        if (!deliberate) {
            log.error("Unexpected error", exception);
            return build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected server error occurred.",
                    null
            );
        }

        HttpStatus status = exception.getMessage().toLowerCase().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return build(status, exception.getMessage(), null);
    }
}
