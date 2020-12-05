package com.electricity.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reading")
    private Double reading;

    @Column(name = "amount_to_pay")
    private Double amountToPay;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
