package com.electricity.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "contract")
    @JoinColumn(name = "customer", referencedColumnName = "id")
    @JsonBackReference
    private Customer customer;

    @Column(name = "rate_per_unit")
    @NotNull
    private double ratePerUnit;

    @Column(name = "rate_after_contract")
    private double rateAfterContract;

    @Column(name = "system_usage_per_unit")
    private double systemUsagePerUnit;

    @Column(name = "rate_after_yearly_cap")
    private double rateAfterYearlyCap;

    @Column(name = "units_used", columnDefinition = "double default 0.0")
    private double unitsUsed;

    @Column(name = "started")
    private LocalDateTime started;

    @Column(name = "ends")
    private LocalDateTime ends;

    @OneToMany(targetEntity = MeterReading.class, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<MeterReading> history;
}
