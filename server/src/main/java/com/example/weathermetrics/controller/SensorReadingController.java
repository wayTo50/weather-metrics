package com.example.weathermetrics.controller;

import com.example.weathermetrics.dto.AverageTemperatureResponse;
import com.example.weathermetrics.dto.RegisterSensorDataRequest;
import com.example.weathermetrics.dto.RegisterSensorDataResponse;
import com.example.weathermetrics.dto.SensorListResponse;
import com.example.weathermetrics.service.SensorReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 *  REST endpoints for sensor readings and temperature requests.
 *  Note:Authentication and authorization are out of scope for this project
 */
@RestController
@RequestMapping("/api/v1")
public class SensorReadingController {
    private final SensorReadingService sensorReadingService;

    public SensorReadingController(SensorReadingService sensorReadingService) {
        this.sensorReadingService = sensorReadingService;
    }

    /** Stores one reading sent by a sensor. */
    @PostMapping("/readings")
    public ResponseEntity<RegisterSensorDataResponse> register(@Valid @RequestBody RegisterSensorDataRequest request) {
        final var response = sensorReadingService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Returns the average temperature for a time range. */
    @GetMapping("/metrics/temperature/average")
    public AverageTemperatureResponse averageTemperature(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(name = "sensorId", required = false) String sensorId) {
        return sensorReadingService.averageTemperature(from, to, sensorId);
    }

    /** Returns the IDs of sensors that have sent readings. */
    @GetMapping("/sensors")
    public SensorListResponse listSensors() {
        return sensorReadingService.listSensors();
    }
}
