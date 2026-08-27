package com.example.weathermetrics.repository;

import com.example.weathermetrics.entity.SensorReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SensorReadingRepositoryTest {
    @Autowired
    SensorReadingRepository repository;

    @Test
    void averageTemperatureWhenMultipleSensorsMatchAndExpectAverage() {
        saveReading("sensor-1", 10.0, "2026-01-01T10:00:00Z");
        saveReading("sensor-1", 20.0, "2026-01-01T11:00:00Z");
        saveReading("sensor-2", 30.0, "2026-01-01T12:00:00Z");

        final var average = repository.averageTemperature(
                instant("2026-01-01T00:00:00Z"), instant("2026-01-02T00:00:00Z"));

        assertThat(average).contains(20.0);
    }

    @Test
    void averageTemperatureForSensorWhenMultipleSensorsMatchAndExpectSelectedSensorAverage() {
        saveReading("sensor-1", 10.0, "2026-01-01T10:00:00Z");
        saveReading("sensor-1", 20.0, "2026-01-01T11:00:00Z");
        saveReading("sensor-2", 30.0, "2026-01-01T12:00:00Z");

        final var average = repository.averageTemperatureForSensor(
                instant("2026-01-01T00:00:00Z"), instant("2026-01-02T00:00:00Z"), "sensor-1");

        assertThat(average).contains(15.0);
    }

    @Test
    void averageTemperatureWhenReadingsAreOnBoundariesAndExpectHalfOpenRange() {
        final var from = instant("2026-01-01T10:00:00Z");
        final var to = instant("2026-01-01T12:00:00Z");
        saveReading("sensor-1", 10.0, from.toString());
        saveReading("sensor-1", 20.0, "2026-01-01T11:00:00Z");
        saveReading("sensor-1", 100.0, to.toString());

        assertThat(repository.averageTemperature(from, to)).contains(15.0);
    }

    @Test
    void averageTemperatureWhenNoReadingsMatchAndExpectEmptyResult() {
        saveReading("sensor-1", 10.0, "2026-01-01T10:00:00Z");

        final var average = repository.averageTemperature(
                instant("2026-01-02T00:00:00Z"), instant("2026-01-03T00:00:00Z"));

        assertThat(average).isEmpty();
    }

    @Test
    void findSensorIdsWhenSensorsHaveMultipleReadingsAndExpectDistinctSortedIds() {
        saveReading("sensor-2", 30.0, "2026-01-01T12:00:00Z");
        saveReading("sensor-1", 10.0, "2026-01-01T10:00:00Z");
        saveReading("sensor-1", 20.0, "2026-01-01T11:00:00Z");

        assertThat(repository.findSensorIds()).containsExactly("sensor-1", "sensor-2");
    }

    private void saveReading(String sensorId, Double temperature, String recordedAt) {
        repository.save(new SensorReading(sensorId, temperature, 50.0, 3.0, instant(recordedAt)));
    }

    private Instant instant(String value) {
        return Instant.parse(value);
    }
}
