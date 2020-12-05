package com.electricity.service;

import com.electricity.dto.ContractDetails;
import com.electricity.exception.UnauthorizedException;
import com.electricity.model.Contract;
import com.electricity.model.Customer;
import com.electricity.model.MeterReading;
import com.electricity.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

    private ContractService contractService;

    @Mock
    private ContractRepository contractRepositoryMock;

    @Mock
    private Principal principalMock;

    private final Supplier<Contract> contractSupplier = () -> {
        Customer customer = new Customer();
        customer.setEmailAddress("test@gmail.com");
        customer.setId(1L);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setUnitsUsed(0.0);
        contract.setSystemUsagePerUnit(2.0);
        contract.setRatePerUnit(10);
        contract.setRateAfterContract(14.0);
        contract.setRateAfterYearlyCap(13.0);
        contract.setStarted(LocalDateTime.now());
        contract.setEnds(LocalDateTime.now().plusYears(1));
        contract.setCustomer(customer);
        customer.setContract(contract);

        Set<MeterReading> readings = new HashSet<>();
        MeterReading meterReading = new MeterReading();
        meterReading.setId(1L);
        meterReading.setTimestamp(LocalDateTime.now());
        meterReading.setReading(10.0d);
        readings.add(meterReading);
        contract.setHistory(readings);

        return contract;
    };

    @BeforeEach
    public void init() {
        contractService = new ContractService(contractRepositoryMock);
    }

    @Test
    public void testGetTotalRatePerUnit() {
        double result = contractService.getTotalRatePerUnit();
        assertEquals(35.306000000000004, result, 0.001d);
    }

    @Test
    public void testGetTotalRatePerUnitAfterContract() {
        double result = contractService.getTotalRatePerUnitAfterContract();
        assertEquals(40.386, result, 0.001d);
    }

    @Test
    public void testGetTotalRatePerUnitAfterYearlyCap() {
        double result = contractService.getRatePerUnitAfterYearlyCap();
        assertEquals(37.846000000000004, result, 0.001d);
    }

    @Test
    public void testGetContractDetailsShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () ->
                contractService.getContractDetailsForCurrentCustomer(principalMock));
    }

    @Test
    public void testGetContractDetailsShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () ->
                contractService.getContractDetailsForCurrentCustomer(null));
    }

    @Test
    public void testGetContractDetailsShouldReturnContractDetailsDto() {
        Contract contract = contractSupplier.get();

        when(principalMock.getName()).thenReturn("test@gmail.com");
        when(contractRepositoryMock.findByCustomerEmailAddress("test@gmail.com")).thenReturn(Optional.of(contract));

        ContractDetails contractDetails = contractService.getContractDetailsForCurrentCustomer(principalMock);

        assertNotNull(contractDetails);
        assertEquals(ContractDetails.class, contractDetails.getClass());
    }
}
