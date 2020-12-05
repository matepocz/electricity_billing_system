package com.electricity.controller;

import com.electricity.dto.ContractDetails;
import com.electricity.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/my-contract")
    public ResponseEntity<ContractDetails> getContractDetailsForCurrentCustomer(Principal principal) {
        ContractDetails contractDetails = contractService.getContractDetailsForCurrentCustomer(principal);
        return new ResponseEntity<>(contractDetails, HttpStatus.OK);
    }
}
