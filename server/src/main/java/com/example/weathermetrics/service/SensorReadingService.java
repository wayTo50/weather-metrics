package com.example.weathermetrics.service;

import com.example.weathermetrics.dto.*;
import com.example.weathermetrics.entity.SensorReading;
import com.example.weathermetrics.exception.InvalidDateRangeException;
import com.example.weathermetrics.mapper.SensorReadingMapper;
import com.example.weathermetrics.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Handles sensor readings and temperature averages requests. */
@Service
public class SensorReadingService {
    private final SensorReadingRepository sensorReadingRepository;
    private final SensorReadingMapper sensorReadingMapper;

    public SensorReadingService(SensorReadingRepository sensorReadingRepository,
                                SensorReadingMapper sensorReadingMapper) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorReadingMapper = sensorReadingMapper;
    }

    @Transactional
    public RegisterSensorDataResponse register(RegisterSensorDataRequest request) {
        final var sensorReading = new SensorReading(request.sensorId().trim(), request.temperatureCelsius(),
                request.humidityPercent(), request.windSpeedMps(), request.recordedAt());
        final var saved = sensorReadingRepository.save(sensorReading);
        return sensorReadingMapper.toRegisterSensorDataResponse(saved);
    }



    @Transactional(readOnly = true)
    public AverageTemperatureResponse averageTemperature(Instant from, Instant to, String sensorId) {
        if (!from.isBefore(to)) {
            throw new InvalidDateRangeException("from must be earlier than to");
        }
        final String id = sensorId == null || sensorId.isBlank() ? null : sensorId.trim();
        final var average = id == null
                ? sensorReadingRepository.averageTemperature(from, to)
                : sensorReadingRepository.averageTemperatureForSensor(from, to, id);
        return new AverageTemperatureResponse(average.orElse(null));
    }

    @Transactional(readOnly = true)
    public SensorListResponse listSensors() {
        return new SensorListResponse(sensorReadingRepository.findSensorIds());
    }
}
