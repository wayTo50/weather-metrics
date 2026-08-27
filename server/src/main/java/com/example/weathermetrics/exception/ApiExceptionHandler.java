package com.example.weathermetrics.exception;

import com.example.weathermetrics.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/** Turns known exceptions into simple API error responses. */
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidDateRangeException.class)
    ResponseEntity<ApiErrorResponse> invalidRange(InvalidDateRangeException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> invalidRequest() {
        return error(HttpStatus.BAD_REQUEST, "Validation failed");
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                Instant.now(), status.value(), message));
    }
}
