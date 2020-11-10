package com.electricity.service;

import com.electricity.dto.MeterReadingResponse;
import com.electricity.model.Contract;
import com.electricity.model.MeterReading;
import com.electricity.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class MeterService {

    private final ContractRepository contractRepository;
    private final ContractService contractService;

    @Autowired
    public MeterService(ContractRepository contractRepository, ContractService contractService) {
        this.contractRepository = contractRepository;
        this.contractService = contractService;
    }

    public MeterReadingResponse calculatePayment(Long contractId, Integer reading) {
        Optional<Contract> contractById = contractRepository.findById(contractId);
        if (contractById.isEmpty()) {
            return null;
        }
        Contract contract = contractById.get();
        MeterReading lastReading = getLastReading(contract);
        double lastMeterReading = lastReading == null ? 0 : lastReading.getReading();
        double unitsUsed = reading - lastMeterReading;
        double unitPrice = getUnitPriceForContract(contract, unitsUsed);
        MeterReadingResponse meterReadingResponse = new MeterReadingResponse();
        meterReadingResponse.setTotalPayment(unitPrice * unitsUsed);

        contract.setUnitsUsed(contract.getUnitsUsed() + unitsUsed);
        addReadingToContractHistory(reading, contract);

        return meterReadingResponse;
    }

    private void addReadingToContractHistory(Integer reading, Contract contract) {
        Set<MeterReading> history = contract.getHistory();
        MeterReading meterReading = new MeterReading();
        meterReading.setReading(reading);
        meterReading.setTimestamp(LocalDateTime.now());
        history.add(meterReading);
    }

    double getUnitPriceForContract(Contract contract, double unitsUsed) {
        if (contract.getEnds().isAfter(LocalDateTime.now()) &&
                contract.getUnitsUsed() + unitsUsed < contractService.getYearlyCap()) {
            return contract.getRatePerUnit();
        } else if (contract.getEnds().isAfter(LocalDateTime.now()) &&
                contract.getUnitsUsed() + unitsUsed > contractService.getYearlyCap()) {
            return contract.getRateAfterYearlyCap();
        } else {
            return contract.getRateAfterContract();
        }
    }

    MeterReading getLastReading(Contract contract) {
        Set<MeterReading> history = contract.getHistory();
        double lastMeterReading = 0;
        MeterReading lastReading = null;
        for (MeterReading meterReading : history) {
            if (meterReading.getReading() > lastMeterReading) {
                lastMeterReading = meterReading.getReading();
                lastReading = meterReading;
            }
        }
        return lastReading;
    }
}
