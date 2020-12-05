package com.electricity.dto;

import com.electricity.model.MeterReading;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MeterReadingDetails {

    private Long id;
    private double reading;
    private LocalDateTime timestamp;

    public MeterReadingDetails(MeterReading meterReading) {
        this.id = meterReading.getId();
        this.reading = meterReading.getReading();
        this.timestamp = meterReading.getTimestamp();
    }
}
