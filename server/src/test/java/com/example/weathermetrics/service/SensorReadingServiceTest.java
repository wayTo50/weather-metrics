package com.example.weathermetrics.service;

import com.example.weathermetrics.dto.RegisterSensorDataRequest;
import com.example.weathermetrics.dto.RegisterSensorDataResponse;
import com.example.weathermetrics.entity.SensorReading;
import com.example.weathermetrics.exception.InvalidDateRangeException;
import com.example.weathermetrics.mapper.SensorReadingMapper;
import com.example.weathermetrics.repository.SensorReadingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceTest {
    @Mock
    SensorReadingRepository repository;
    @Mock
    SensorReadingMapper mapper;
    @InjectMocks
    SensorReadingService service;

    static Stream<String> missingSensorIds() {
        return Stream.of(null, "", " ", "   ");
    }

    static Stream<Arguments> invalidRanges() {
        final var earlier = Instant.parse("2026-01-01T00:00:00Z");
        final var later = Instant.parse("2026-01-02T00:00:00Z");
        return Stream.of(Arguments.of(earlier, earlier),
                Arguments.of(later, earlier));
    }

    @Test
    void registerWhenRequestIsValidAndExpectMappedResponse() {
        final var request = new RegisterSensorDataRequest(
                "sensor-1", 10.0, 50.0, 3.0, Instant.parse("2026-01-01T10:00:00Z"));
        final var saved = mock(SensorReading.class);
        final var response = new RegisterSensorDataResponse(UUID.randomUUID(), "sensor-1");
        when(repository.save(any(SensorReading.class))).thenReturn(saved);
        when(mapper.toRegisterSensorDataResponse(saved)).thenReturn(response);

        assertThat(service.register(request)).isEqualTo(response);
        verify(mapper).toRegisterSensorDataResponse(saved);
    }

    @Test
    void averageTemperatureWhenSensorIdIsProvidedAndExpectSensorAverage() {
        final var from = Instant.parse("2026-01-01T00:00:00Z");
        final var to = Instant.parse("2026-01-02T00:00:00Z");
        when(repository.averageTemperatureForSensor(from, to, "sensor-1")).thenReturn(Optional.of(12.5));

        final var response = service.averageTemperature(from, to, "  sensor-1  ");

        assertThat(response.averageTemperatureCelsius()).isEqualTo(12.5);
        verify(repository).averageTemperatureForSensor(from, to, "sensor-1");
    }

    @ParameterizedTest
    @MethodSource("missingSensorIds")
    void averageTemperatureWhenSensorIdIsMissingAndExpectAllSensorAverage(String sensorId) {
        final var from = Instant.parse("2026-01-01T00:00:00Z");
        final var to = Instant.parse("2026-01-02T00:00:00Z");
        when(repository.averageTemperature(from, to)).thenReturn(Optional.of(15.0));

        final var response = service.averageTemperature(from, to, sensorId);

        assertThat(response.averageTemperatureCelsius()).isEqualTo(15.0);
        verify(repository).averageTemperature(from, to);
    }

    @ParameterizedTest
    @MethodSource("invalidRanges")
    void averageTemperatureWhenDateRangeIsInvalidAndExpectException(Instant from, Instant to) {
        assertThatThrownBy(() -> service.averageTemperature(from, to, null))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessage("from must be earlier than to");
        verifyNoInteractions(repository);
    }

    @Test
    void averageTemperatureWhenNoReadingsMatchAndExpectNullAverage() {
        final var from = Instant.parse("2026-01-01T00:00:00Z");
        final var to = Instant.parse("2026-01-02T00:00:00Z");
        when(repository.averageTemperature(from, to)).thenReturn(Optional.empty());

        assertThat(service.averageTemperature(from, to, null).averageTemperatureCelsius()).isNull();
    }

    @Test
    void listSensorsWhenCalledAndExpectSensorIds() {
        when(repository.findSensorIds()).thenReturn(List.of("sensor-1", "sensor-2"));

        assertThat(service.listSensors().sensorIds()).containsExactly("sensor-1", "sensor-2");
    }
}
