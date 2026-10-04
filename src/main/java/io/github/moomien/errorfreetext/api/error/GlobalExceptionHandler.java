package io.github.moomien.errorfreetext.api.error;

import io.github.moomien.errorfreetext.service.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.
            getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(
            TaskNotFoundException ex,
            HttpServletRequest request) {

        return build(ErrorCode.TASK_NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return build(ErrorCode.VALIDATION_FAILED, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return build(
                ErrorCode.MALFORMED_REQUEST,
                "Malformed request body or invalid value. Allowed languages: EN, RU",
                request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return build(
                ErrorCode.INVALID_PATH_PARAMETER,
                "Invalid value '%s' for parameter '%s'".formatted(ex.getValue(), ex.getName()),
                request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(
            NoResourceFoundException ex,
            HttpServletRequest request) {
        return build(ErrorCode.RESOURCE_NOT_FOUND, "Resource not found", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        return build(ErrorCode.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return build(ErrorCode.INTERNAL_ERROR, "Internal server error", request);
    }

    private ResponseEntity<ErrorResponse> build(
            ErrorCode code,
            String message,
            HttpServletRequest request){
        if (code.status().is4xxClientError()) {
            log.warn("{} {} -> {} : {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    code.code(),
                    message
            );
        }

        ErrorResponse body = new ErrorResponse(
                message,
                code.code(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(code.status()).body(body);
    }
}
