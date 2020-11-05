package com.electricity;

import com.electricity.repository.ContractRepository;
import com.electricity.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

    private ContractService contractService;

    @Mock
    private ContractRepository contractRepositoryMock;

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
        assertEquals(58.8, result, 0.001d);
    }

    @Test
    public void testGetTotalRatePerUnitAfterYearlyCap() {
        double result = contractService.getRatePerUnitAfterYearlyCap();
        assertEquals(37.846000000000004, result, 0.001d);
    }
}
