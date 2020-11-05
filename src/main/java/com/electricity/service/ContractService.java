package com.electricity.service;

import com.electricity.model.Contract;
import com.electricity.model.Customer;
import com.electricity.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class ContractService {

    private static final double RATE_PER_UNIT = 11.90;
    private static final double RATE_PER_UNIT_AFTER_YEARLY_CAP = 13.90;
    private static final double RATE_PER_UNIT_AFTER_CONTRACT = 15.90;
    private static final double SYSTEM_USAGE_PER_UNIT = 15.90;
    private static final double VAT = 27;
    private static final int YEARLY_CAP = 1100;

    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public Contract createNewContract(Customer customer) {
        Contract contract = new Contract();
        contract.setCustomer(customer);
        contract.setStarted(LocalDateTime.now());
        contract.setEnds(LocalDateTime.now().plusYears(1));
        contract.setRatePerUnit(getTotalRatePerUnit());
        contract.setRateAfterContract(getTotalRatePerUnitAfterContract());
        contract.setSystemUsagePerUnit(SYSTEM_USAGE_PER_UNIT);
        return contractRepository.save(contract);
    }

    public double getTotalRatePerUnit() {
        return (RATE_PER_UNIT + SYSTEM_USAGE_PER_UNIT) * (VAT / 100 + 1);
    }

    public double getTotalRatePerUnitAfterContract() {
        return RATE_PER_UNIT_AFTER_CONTRACT + SYSTEM_USAGE_PER_UNIT + VAT;
    }

    public double getRatePerUnitAfterYearlyCap() {
        return (RATE_PER_UNIT_AFTER_YEARLY_CAP + SYSTEM_USAGE_PER_UNIT) * (VAT / 100 + 1);
    }
}
