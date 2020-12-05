package com.electricity.dto;

import com.electricity.model.Contract;
import com.electricity.util.MeterReadingComparator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ContractDetails {

    private Long id;
    private double ratePerUnit;
    private double rateAfterContract;
    private double systemUsagePerUnit;
    private double rateAfterYearlyCap;
    private double unitsUsed;
    private LocalDateTime started;
    private LocalDateTime ends;
    private List<MeterReadingDetails> history;

    public ContractDetails(Contract contract) {
        this.id = contract.getId();
        this.ratePerUnit = contract.getRatePerUnit();
        this.rateAfterContract = contract.getRateAfterContract();
        this.systemUsagePerUnit = contract.getSystemUsagePerUnit();
        this.rateAfterYearlyCap = contract.getRateAfterYearlyCap();
        this.unitsUsed = contract.getUnitsUsed();
        this.started = contract.getStarted();
        this.ends = contract.getEnds();
        this.history = contract.getHistory()
                .stream()
                .sorted(new MeterReadingComparator())
                .map(MeterReadingDetails::new)
                .collect(Collectors.toList());
    }
}
