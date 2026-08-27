package com.example.weathermetrics.exception;

/** Raised when the start of a time range is not before the end. */
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
