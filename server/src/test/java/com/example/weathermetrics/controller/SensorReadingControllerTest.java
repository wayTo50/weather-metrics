package com.example.weathermetrics.controller;

import com.example.weathermetrics.dto.ApiErrorResponse;
import com.example.weathermetrics.dto.AverageTemperatureResponse;
import com.example.weathermetrics.dto.RegisterSensorDataRequest;
import com.example.weathermetrics.dto.RegisterSensorDataResponse;
import com.example.weathermetrics.dto.SensorListResponse;
import com.example.weathermetrics.exception.ApiExceptionHandler;
import com.example.weathermetrics.exception.InvalidDateRangeException;
import com.example.weathermetrics.service.SensorReadingService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SensorReadingControllerTest {
    @Mock
    SensorReadingService service;
    MockMvc mockMvc;
    JsonMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SensorReadingController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void registerWhenRequestIsValidAndExpectCreatedResponse() throws Exception {
        final var id = UUID.randomUUID();
        final var expected = new RegisterSensorDataResponse(id, "sensor-1");
        final var request = new RegisterSensorDataRequest(
                "sensor-1", 10.0, 50.0, 3.0, Instant.parse("2026-01-01T10:00:00Z"));
        when(service.register(request)).thenReturn(expected);

        final var result = mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(readResponse(result, RegisterSensorDataResponse.class)).isEqualTo(expected);
        verify(service).register(request);
    }

    @Test
    void registerWhenRequestIsInvalidAndExpectBadRequest() throws Exception {
        final var result = mockMvc.perform(post("/api/v1/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        final var response = readResponse(result, ApiErrorResponse.class);
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.message()).isEqualTo("Validation failed");
        verifyNoInteractions(service);
    }

    @Test
    void averageTemperatureWhenRequestIsValidAndExpectAverageResponse() throws Exception {
        final var from = Instant.parse("2026-01-01T00:00:00Z");
        final var to = Instant.parse("2026-01-02T00:00:00Z");
        final var expected = new AverageTemperatureResponse(12.5);
        when(service.averageTemperature(from, to, "sensor-1")).thenReturn(expected);

        final var result = mockMvc.perform(get("/api/v1/metrics/temperature/average")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("sensorId", "sensor-1"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(readResponse(result, AverageTemperatureResponse.class)).isEqualTo(expected);
        verify(service).averageTemperature(from, to, "sensor-1");
    }

    @Test
    void averageTemperatureWhenDateRangeIsInvalidAndExpectBadRequest() throws Exception {
        final var time = Instant.parse("2026-01-01T00:00:00Z");
        when(service.averageTemperature(time, time, null))
                .thenThrow(new InvalidDateRangeException("from must be earlier than to"));

        final var result = mockMvc.perform(get("/api/v1/metrics/temperature/average")
                        .param("from", time.toString())
                        .param("to", time.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

        final var response = readResponse(result, ApiErrorResponse.class);
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.message()).isEqualTo("from must be earlier than to");
    }

    @Test
    void listSensorsWhenCalledAndExpectSensorIds() throws Exception {
        final var expected = new SensorListResponse(List.of("sensor-1", "sensor-2"));
        when(service.listSensors()).thenReturn(expected);

        final var result = mockMvc.perform(get("/api/v1/sensors"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(readResponse(result, SensorListResponse.class)).isEqualTo(expected);
    }

    private <T> T readResponse(MvcResult result, Class<T> responseType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), responseType);
    }
}
