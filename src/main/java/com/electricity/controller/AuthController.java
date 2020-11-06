package com.electricity.controller;

import com.electricity.dto.LoginRequest;
import com.electricity.dto.LoginResponse;
import com.electricity.dto.NewCustomerRequest;
import com.electricity.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = customerService.login(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
