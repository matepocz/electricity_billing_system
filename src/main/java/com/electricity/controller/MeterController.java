package com.electricity.controller;

import com.electricity.dto.MeterReadingResponse;
import com.electricity.service.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meters")
public class MeterController {

    private final MeterService meterService;

    @Autowired
    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping("/{contractId}")
    public ResponseEntity<MeterReadingResponse> calculatePayment(
            @PathVariable("contractId") Long contractId, @RequestParam("reading") Integer reading) {
        MeterReadingResponse response = meterService.calculatePayment(contractId, reading);
        return response == null ?
                new ResponseEntity<>(HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>(response, HttpStatus.OK);
    }
}
