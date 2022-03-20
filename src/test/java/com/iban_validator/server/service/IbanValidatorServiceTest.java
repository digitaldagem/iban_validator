package com.iban_validator.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IbanValidatorServiceTest {

    static IbanValidatorService ibanValidatorService;
    String iban;
    ResponseEntity<?> responseEntity;

    @BeforeEach
    void setUp() {
        ibanValidatorService = new IbanValidatorService();
    }

    @Test
    void checkIban_withListOfValidIbansFromATextFile_returnsOKforEach() throws IOException {
        // given
        File file = new File("src/test/resources/list_of_valid_IBANs.txt");
        BufferedReader br
                = new BufferedReader(new FileReader(file));
        while ((iban = br.readLine()) != null) {
            // when
            responseEntity = ibanValidatorService.validateIban(iban);

            // then
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        }
    }

    @Test
    void checkIban_withListOfInvalidIbansFromATextFile_returnsBAD_REQUESTforEach() throws IOException {
        // given
        File file = new File("src/test/resources/list_of_invalid_IBANs.txt");
        BufferedReader br
                = new BufferedReader(new FileReader(file));
        while ((iban = br.readLine()) != null) {
            // when
            responseEntity = ibanValidatorService.validateIban(iban);

            // then
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        }
    }

    @Test
    void checkIban_withAnInvalidCountryCode_returnsBAD_REQUEST() {
        // given
        iban = "ZE7280000810340009783242";

        // when
        responseEntity = ibanValidatorService.validateIban(iban);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(iban.substring(0, 2) + " is not a valid IBAN country code.",
                responseEntity.getBody());
    }

    @Test
    void checkIban_withTooLongALength_returnsBAD_REQUEST() {
        // given
        iban = "SE72800008103400097832422";

        // when
        responseEntity = ibanValidatorService.validateIban(iban);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(iban + " with country code " + iban.substring(0, 2) + " does not have a valid length.",
                responseEntity.getBody());
    }

    @Test
    void checkIban_withACheckSumNotHavingAModulusOfOne_returnsBAD_REQUEST() {
        // given
        iban = "SE7280700810340009783242";

        // when
        responseEntity = ibanValidatorService.validateIban(iban);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(iban + "'s checkSum does not have a modulus of 1.",
                responseEntity.getBody());
    }
}
