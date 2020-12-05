package com.electricity.service;

import com.electricity.dto.ContractDetails;
import com.electricity.exception.UnauthorizedException;
import com.electricity.model.Contract;
import com.electricity.model.Customer;
import com.electricity.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ContractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractService.class);

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

    /**
     * Creates a new contract.
     *
     * @param customer The given customer the contract made for.
     * @return A Contract object.
     */
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

    /**
     * Attempts to fetch a Contract object from the database based on the
     * currently logged in user's email address.
     *
     * @param principal The currently logged in user
     * @return A ContractDetails DTO
     */
    public ContractDetails getContractDetailsForCurrentCustomer(Principal principal) {
        if (principal == null) {
            LOGGER.info("Unauthenticated customer requested contract details");
            throw new UnauthorizedException("Unauthorized request!");
        }
        Optional<Contract> optionalContract = contractRepository.findByCustomerEmailAddress(principal.getName());
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            return new ContractDetails(contract);
        }
        LOGGER.info("Contract not found for customer: {}", principal.getName());
        throw new EntityNotFoundException("Contract not found!");
    }

    public double getTotalRatePerUnit() {
        return (RATE_PER_UNIT + SYSTEM_USAGE_PER_UNIT) * (VAT / 100 + 1);
    }

    public double getTotalRatePerUnitAfterContract() {
        return (RATE_PER_UNIT_AFTER_CONTRACT + SYSTEM_USAGE_PER_UNIT) * (VAT / 100 + 1);
    }

    public double getRatePerUnitAfterYearlyCap() {
        return (RATE_PER_UNIT_AFTER_YEARLY_CAP + SYSTEM_USAGE_PER_UNIT) * (VAT / 100 + 1);
    }

    public int getYearlyCap() {
        return YEARLY_CAP;
    }

    public double getVAT() {
        return VAT;
    }

    public double getSystemUsagePerUnit() {
        return SYSTEM_USAGE_PER_UNIT;
    }
}
