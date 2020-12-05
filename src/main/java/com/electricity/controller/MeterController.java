package com.electricity.controller;

import com.electricity.dto.MeterReadingResponse;
import com.electricity.service.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/meters")
public class MeterController {

    private final MeterService meterService;

    @Autowired
    public MeterController(MeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping("/send-reading")
    public ResponseEntity<MeterReadingResponse> calculatePayment(@RequestParam("reading") Integer reading,
                                                                 Principal principal) {
        MeterReadingResponse response = meterService.makeMeterReading(reading, principal);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
