package com.electricity.service;

import com.electricity.dto.MeterReadingResponse;
import com.electricity.exception.UnauthorizedException;
import com.electricity.model.Contract;
import com.electricity.model.MeterReading;
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
import java.util.Set;

@Service
@Transactional
public class MeterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterService.class);

    private final ContractRepository contractRepository;
    private final ContractService contractService;

    @Autowired
    public MeterService(ContractRepository contractRepository, ContractService contractService) {
        this.contractRepository = contractRepository;
        this.contractService = contractService;
    }

    /**
     * Calculates the payment based on the contract details and the given meter reading.
     *
     * @param reading   The actual meter reading.
     * @param principal The currently logged in customer.
     * @return A MeterReadingResponse DTO, containing the amount to pay, and the deadline.
     */
    public MeterReadingResponse makeMeterReading(Integer reading, Principal principal) {
        if (principal == null) {
            LOGGER.info("Unauthenticated user posted a meter reading!");
            throw new UnauthorizedException("Unauthorized request!");
        }

        Optional<Contract> optionalContract = contractRepository.findByCustomerEmailAddress(principal.getName());
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            MeterReading lastReading = getLastReading(contract);
            double lastMeterReading = lastReading == null ? 0 : lastReading.getReading();
            if (lastMeterReading > reading) {
                throw new IllegalArgumentException("Your actual reading is lower than your last reading!");
            }
            double unitsUsedSinceLastReading = reading - lastMeterReading;
            double unitPrice = getUnitPriceForContract(contract, unitsUsedSinceLastReading);
            double amountToPay = unitPrice * unitsUsedSinceLastReading;

            MeterReadingResponse meterReadingResponse = createMeterReadingResponse(amountToPay);

            MeterReading meterReading = createMeterReading(reading, amountToPay);
            updateContractDetails(contract, unitsUsedSinceLastReading, meterReading);
            return meterReadingResponse;
        }
        LOGGER.info("Contract not found for customer: {}", principal.getName());
        throw new EntityNotFoundException("Contract not found!");
    }

    private void updateContractDetails(Contract contract, double unitsUsedSinceLastReading, MeterReading meterReading) {
        contract.setUnitsUsed(contract.getUnitsUsed() + unitsUsedSinceLastReading);
        contract.getHistory().add(meterReading);
    }

    /**
     * Creates a MeterReadingResponse object.
     *
     * @param amountToPay The amount to pay calculated by meter reading and contract details.
     * @return A MeterReadingResponse object.
     */
    MeterReadingResponse createMeterReadingResponse(double amountToPay) {
        MeterReadingResponse meterReadingResponse = new MeterReadingResponse();
        meterReadingResponse.setTotalPayment(amountToPay);
        meterReadingResponse.setPaymentDeadline(LocalDateTime.now().plusDays(15));
        return meterReadingResponse;
    }

    /**
     * Creates a MeterReading object.
     *
     * @param reading     The currently saved meter reading given by the customer.
     * @param amountToPay The total amount to pay based on usage and the contract details.
     * @return A MeterReading object.
     */
    MeterReading createMeterReading(Integer reading, double amountToPay) {
        MeterReading meterReading = new MeterReading();
        meterReading.setReading(reading.doubleValue());
        meterReading.setAmountToPay(amountToPay);
        meterReading.setTimestamp(LocalDateTime.now());
        return meterReading;
    }

    /**
     * Retrieves the amount to pay per unit based on the contract details
     *
     * @param contract  The actual Contract object.
     * @param unitsUsed The amount of units the customer used since the last reading.
     * @return The actual price per unit.
     */
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

    /**
     * Attempts to fetch the last meter reading.
     *
     * @param contract The actual Contract object
     * @return A MeterReading object, null if not found.
     */
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
