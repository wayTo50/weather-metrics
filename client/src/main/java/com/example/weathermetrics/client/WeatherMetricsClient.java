package com.example.weathermetrics.client;

import com.example.weathermetrics.dto.AverageTemperatureResponse;
import com.example.weathermetrics.dto.RegisterSensorDataRequest;
import com.example.weathermetrics.dto.RegisterSensorDataResponse;
import com.example.weathermetrics.dto.SensorListResponse;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Instant;

/** Small Java client for calling the weather metrics API. */
public class WeatherMetricsClient {
    private final RestClient restClient;

    public WeatherMetricsClient(String baseUrl) {
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public RegisterSensorDataResponse registerSensorData(RegisterSensorDataRequest request) {
        return restClient.post().uri("/api/v1/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request).retrieve().body(RegisterSensorDataResponse.class);
    }

    public AverageTemperatureResponse averageTemperature(Instant from, Instant to) {
        return averageTemperature(from, to, null);
    }

    public AverageTemperatureResponse averageTemperature(Instant from, Instant to, String sensorId) {
        final var request = restClient.get().uri(uriBuilder -> {
            final var requestUri = uriBuilder
                    .path("/api/v1/metrics/temperature/average")
                    .queryParam("from", from)
                    .queryParam("to", to);

            if (sensorId != null) {
                requestUri.queryParam("sensorId", sensorId);
            }

            return requestUri.build();
        });

        return request.retrieve().body(AverageTemperatureResponse.class);
    }

    public SensorListResponse listSensors() {
        return restClient.get().uri("/api/v1/sensors").retrieve().body(SensorListResponse.class);
    }
}
