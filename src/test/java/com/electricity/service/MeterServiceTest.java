package com.electricity.service;

import com.electricity.dto.MeterReadingResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeterServiceTest {

    private MeterService meterService;

    @Mock
    private ContractRepository contractRepositoryMock;

    @Mock
    private ContractService contractServiceMock;

    @Mock
    private Principal principalMock;

    @BeforeEach
    public void init() {
        meterService = new MeterService(contractRepositoryMock, contractServiceMock);
    }

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

    @Test
    public void testGetUnitPriceForContractBeforeContractEnds() {
        when(contractServiceMock.getYearlyCap()).thenReturn(1100);

        double actualResult = meterService.getUnitPriceForContract(contractSupplier.get(), 10);
        assertEquals(10.0, actualResult);
    }

    @Test
    public void testGetUnitPriceForContractAfterYearlyCapExceeded() {
        when(contractServiceMock.getYearlyCap()).thenReturn(0);
        double actualResult = meterService.getUnitPriceForContract(contractSupplier.get(), 10);
        assertEquals(13.0, actualResult);
    }

    @Test
    public void testGetUnitPriceForContractAfterContractEnds() {
        Contract contract = contractSupplier.get();
        contract.setEnds(LocalDateTime.now().minusMinutes(1));
        double actualResult = meterService.getUnitPriceForContract(contract, 10);
        assertEquals(14.0, actualResult);
    }

    @Test
    public void testGetLastReadingShouldReturnNull() {
        Contract contract = contractSupplier.get();
        contract.setHistory(new HashSet<>());
        MeterReading lastReading = meterService.getLastReading(contract);
        assertNull(lastReading);
    }

    @Test
    public void testGetLastReadingShouldReturnTen() {
        Contract contract = contractSupplier.get();

        MeterReading lastReading = meterService.getLastReading(contract);

        assertNotNull(lastReading);
        assertEquals(10.0, lastReading.getReading());
    }

    @Test
    public void testCalculatePaymentShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class, () -> meterService.makeMeterReading(10, null));
    }

    @Test
    public void testCalculatePaymentShouldThrowEntityNotFoundException() {
        when(principalMock.getName()).thenReturn("test@gmail.com");

        assertThrows(EntityNotFoundException.class, () -> meterService.makeMeterReading(10, principalMock));
    }

    @Test
    public void testCalculatePaymentShouldThrowIllegalArgumentException() {
        Contract contract = contractSupplier.get();

        when(principalMock.getName()).thenReturn("test@gmail.com");
        when(contractRepositoryMock.findByCustomerEmailAddress("test@gmail.com")).thenReturn(Optional.of(contract));

        assertThrows(IllegalArgumentException.class, () -> meterService.makeMeterReading(-1, principalMock));
    }

    @Test
    public void testCalculatePaymentBeforeYearlyCap() {
        Contract contract = contractSupplier.get();

        when(principalMock.getName()).thenReturn("test@gmail.com");
        when(contractRepositoryMock.findByCustomerEmailAddress("test@gmail.com")).thenReturn(Optional.of(contract));
        when(contractServiceMock.getYearlyCap()).thenReturn(1100);
        MeterReadingResponse meterReadingResponse = meterService.makeMeterReading(20, principalMock);

        assertNotNull(meterReadingResponse);
        assertEquals(100.0, meterReadingResponse.getTotalPayment());
    }

    @Test
    public void testCalculatePaymentAfterYearlyCapExceeded() {
        Contract contract = contractSupplier.get();

        when(principalMock.getName()).thenReturn("test@gmail.com");
        when(contractRepositoryMock.findByCustomerEmailAddress("test@gmail.com")).thenReturn(Optional.of(contract));
        MeterReadingResponse meterReadingResponse = meterService.makeMeterReading(20, principalMock);

        assertNotNull(meterReadingResponse);
        assertEquals(130.0, meterReadingResponse.getTotalPayment());
    }

    @Test
    public void testCalculatePaymentAfterContractEnded() {
        Contract contract = contractSupplier.get();
        contract.setEnds(LocalDateTime.now().minusMinutes(1));

        when(principalMock.getName()).thenReturn("test@gmail.com");
        when(contractRepositoryMock.findByCustomerEmailAddress("test@gmail.com")).thenReturn(Optional.of(contract));
        MeterReadingResponse meterReadingResponse = meterService.makeMeterReading(20, principalMock);

        assertNotNull(meterReadingResponse);
        assertEquals(140.0, meterReadingResponse.getTotalPayment());
    }
}
