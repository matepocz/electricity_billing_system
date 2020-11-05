package com.electricity.controller;

import com.electricity.dto.NewCustomerRequest;
import com.electricity.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CustomerService customerService;

    @Autowired
    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody NewCustomerRequest newCustomerRequest) {
        boolean result = customerService.signup(newCustomerRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
