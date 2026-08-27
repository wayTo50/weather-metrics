package com.example.weathermetrics.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "sensor_id", nullable = false, length = 100)
    private String sensorId;
    @Column(name = "temperature_celsius", nullable = false)
    private Double temperatureCelsius;
    @Column(name = "humidity_percent", nullable = false)
    private Double humidityPercent;
    @Column(name = "wind_speed_mps", nullable = false)
    private Double windSpeedMps;
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
    protected SensorReading() {
    }

    public SensorReading(String sensorId, Double temperatureCelsius, Double humidityPercent,
                         Double windSpeedMps, Instant recordedAt) {
        this.sensorId = sensorId;
        this.temperatureCelsius = temperatureCelsius;
        this.humidityPercent = humidityPercent;
        this.windSpeedMps = windSpeedMps;
        this.recordedAt = recordedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

}
