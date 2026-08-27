package com.example.weathermetrics.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterSensorDataRequestValidationTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    static Stream<String> invalidSensorIds() {
        return Stream.of(null, "", " ", "   ", "s".repeat(101));
    }

    static Stream<Arguments> requestsWithMissingFields() {
        final var recordedAt = Instant.parse("2026-01-01T10:00:00Z");
        return Stream.of(
                Arguments.of(new RegisterSensorDataRequest("sensor-1", null, 50.0, 3.0, recordedAt),
                        "temperatureCelsius"),
                Arguments.of(new RegisterSensorDataRequest("sensor-1", 10.0, null, 3.0, recordedAt),
                        "humidityPercent"),
                Arguments.of(new RegisterSensorDataRequest("sensor-1", 10.0, 50.0, null, recordedAt),
                        "windSpeedMps"),
                Arguments.of(new RegisterSensorDataRequest("sensor-1", 10.0, 50.0, 3.0, null),
                        "recordedAt")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidSensorIds")
    void validateWhenSensorIdIsInvalidAndExpectViolation(String sensorId) {
        assertViolation(validRequest(sensorId), "sensorId");
    }

    @ParameterizedTest
    @MethodSource("requestsWithMissingFields")
    void validateWhenRequiredFieldIsMissingAndExpectViolation(
            RegisterSensorDataRequest request, String field) {
        assertViolation(request, field);
    }

    @Test
    void validateWhenRequestIsValidAndExpectNoViolations() {
        assertThat(VALIDATOR.validate(validRequest("sensor-1"))).isEmpty();
    }

    private RegisterSensorDataRequest validRequest(String sensorId) {
        return new RegisterSensorDataRequest(sensorId, 10.0, 50.0, 3.0,
                Instant.parse("2026-01-01T10:00:00Z"));
    }

    private void assertViolation(RegisterSensorDataRequest request, String field) {
        assertThat(VALIDATOR.validate(request))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(field));
    }
}
