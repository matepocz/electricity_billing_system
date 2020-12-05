package com.electricity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MeterReadingResponse {

    private double totalPayment;
    private LocalDateTime paymentDeadline;
}
