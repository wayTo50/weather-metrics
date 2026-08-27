package com.example.weathermetrics.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record RegisterSensorDataRequest(@NotBlank @Size(max = 100) String sensorId,
                                        @NotNull Double temperatureCelsius,
                                        @NotNull Double humidityPercent,
                                        @NotNull Double windSpeedMps,
                                        @NotNull Instant recordedAt) {
}
