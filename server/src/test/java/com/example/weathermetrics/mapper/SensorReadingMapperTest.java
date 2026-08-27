package com.example.weathermetrics.mapper;

import com.example.weathermetrics.entity.SensorReading;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorReadingMapperTest {
    private final SensorReadingMapper mapper = new SensorReadingMapper();

    @Test
    void toRegisterSensorDataResponseWhenReadingIsProvidedAndExpectMappedResponse() {
        final var id = UUID.randomUUID();
        final var reading = mock(SensorReading.class);
        when(reading.getId()).thenReturn(id);
        when(reading.getSensorId()).thenReturn("sensor-1");

        final var response = mapper.toRegisterSensorDataResponse(reading);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.sensorId()).isEqualTo("sensor-1");
    }
}
