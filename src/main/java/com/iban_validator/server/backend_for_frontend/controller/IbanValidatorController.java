package com.iban_validator.server.backend_for_frontend.controller;

import com.iban_validator.server.service.IbanValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validate_iban")
public class IbanValidatorController {

    private final IbanValidatorService ibanValidatorService;

    @Autowired
    public IbanValidatorController(IbanValidatorService ibanValidatorService) {
        this.ibanValidatorService = ibanValidatorService;
    }

    @GetMapping("/{iban}")
    public ResponseEntity<?> validateIban(@PathVariable("iban") String iban) {
        return ResponseEntity.ok().body(ibanValidatorService.validateIban(iban));
    }
}
