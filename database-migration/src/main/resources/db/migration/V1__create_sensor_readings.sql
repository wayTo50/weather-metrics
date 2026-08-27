CREATE TABLE sensor_readings
(
    id                  UUID                     PRIMARY KEY,
    sensor_id           VARCHAR(100)             NOT NULL,
    temperature_celsius DOUBLE PRECISION         NOT NULL,
    humidity_percent    DOUBLE PRECISION         NOT NULL,
    wind_speed_mps      DOUBLE PRECISION         NOT NULL,
    recorded_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    received_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sensor_readings_recorded_at
    ON sensor_readings (recorded_at);

CREATE INDEX idx_sensor_readings_sensor_recorded
    ON sensor_readings (sensor_id, recorded_at);
