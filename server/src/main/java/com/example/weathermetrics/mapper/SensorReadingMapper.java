package com.example.weathermetrics.mapper;

import com.example.weathermetrics.dto.RegisterSensorDataResponse;
import com.example.weathermetrics.entity.SensorReading;
import org.springframework.stereotype.Component;

/** Converts stored reading entity into API responses. */
@Component
public class SensorReadingMapper {
    public RegisterSensorDataResponse toRegisterSensorDataResponse(SensorReading reading) {
        return new RegisterSensorDataResponse(reading.getId(), reading.getSensorId());
    }
}
