package com.example.weathermetrics.dto;

import java.time.Instant;

/**
 * General API error response
 * @param timestamp timestamp
 * @param status http response code
 * @param message error message
 */
public record ApiErrorResponse(Instant timestamp, int status, String message) {
}
