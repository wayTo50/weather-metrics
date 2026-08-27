package com.example.weathermetrics.integration;

import com.example.weathermetrics.WeatherMetricsApplication;
import com.example.weathermetrics.client.WeatherMetricsClient;
import com.example.weathermetrics.dto.RegisterSensorDataRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WeatherMetricsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherMetricsIntegrationTest {
    @LocalServerPort
    int port;

    WeatherMetricsClient client;

    @BeforeEach
    void setup() {
        client = new WeatherMetricsClient("http://localhost:" + port);
    }

    @Test
    void registerSensorDataWhenRequestIsValidAndExpectReadingRegistered() {
        final var response = client.registerSensorData(
                request("register-test", 10.0, "2026-01-01T10:00:00Z"));

        assertThat(response.id()).isNotNull();
        assertThat(response.sensorId()).isEqualTo("register-test");
    }

    @Test
    void averageTemperatureWhenReadingsExistAndExpectAverageForAllSensors() {
        client.registerSensorData(request("average-all-1", 10.0, "2026-02-01T10:00:00Z"));
        client.registerSensorData(request("average-all-2", 20.0, "2026-02-01T11:00:00Z"));

        final var response = client.averageTemperature(
                Instant.parse("2026-02-01T00:00:00Z"),
                Instant.parse("2026-02-02T00:00:00Z"));

        assertThat(response.averageTemperatureCelsius()).isEqualTo(15.0);
    }

    @Test
    void averageTemperatureWhenSensorIdIsProvidedAndExpectAverageForSensor() {
        client.registerSensorData(request("average-sensor-1", 10.0, "2026-03-01T10:00:00Z"));
        client.registerSensorData(request("average-sensor-1", 20.0, "2026-03-01T11:00:00Z"));
        client.registerSensorData(request("average-sensor-2", 30.0, "2026-03-01T12:00:00Z"));

        final var response = client.averageTemperature(
                Instant.parse("2026-03-01T00:00:00Z"),
                Instant.parse("2026-03-02T00:00:00Z"),
                "average-sensor-1");

        assertThat(response.averageTemperatureCelsius()).isEqualTo(15.0);
    }

    @Test
    void listSensorsWhenReadingsExistAndExpectSensorIds() {
        client.registerSensorData(request("list-test-1", 10.0, "2026-04-01T10:00:00Z"));
        client.registerSensorData(request("list-test-2", 20.0, "2026-04-01T11:00:00Z"));

        final var response = client.listSensors();

        assertThat(response.sensorIds()).contains("list-test-1", "list-test-2");
    }

    private RegisterSensorDataRequest request(String sensorId, double temperature, String recordedAt) {
        return new RegisterSensorDataRequest(sensorId, temperature, 60.0, 4.0, Instant.parse(recordedAt));
    }
}
