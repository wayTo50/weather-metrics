package com.example.weathermetrics.repository;

import com.example.weathermetrics.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Reads and writes sensor readings. */
public interface SensorReadingRepository extends JpaRepository<SensorReading, UUID> {
    @Query("select avg(r.temperatureCelsius) from SensorReading r where r.recordedAt >= :from and r.recordedAt < :to")
    Optional<Double> averageTemperature(@Param("from") Instant from, @Param("to") Instant to);

    @Query("select avg(r.temperatureCelsius) from SensorReading r where r.recordedAt >= :from and r.recordedAt < :to and r.sensorId = :sensorId")
    Optional<Double> averageTemperatureForSensor(@Param("from") Instant from, @Param("to") Instant to,
                                                 @Param("sensorId") String sensorId);

    @Query("select distinct r.sensorId from SensorReading r order by r.sensorId")
    List<String> findSensorIds();
}
