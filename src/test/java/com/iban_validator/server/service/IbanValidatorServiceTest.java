package com.iban_validator.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IbanValidatorServiceTest {

    static IbanValidatorService ibanValidatorService;
    ResponseEntity<?> responseEntity;

    @BeforeEach
    void setUp() {
        ibanValidatorService = new IbanValidatorService();
    }

    @Test
    void checkIban_withListOfValidIbansFromATextFile_returnsOKforEach() throws IOException {
        // given
        List<String> ibans = readLinesFromFile("src/test/resources/list_of_valid_IBANs.txt");

        for (String iban : ibans) {
            // when
            responseEntity = ibanValidatorService.validateIban(iban);

            // then
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), () -> "Failed for IBAN: " + iban);
        }
    }

    @Test
    void checkIban_withListOfInvalidIbansFromATextFile_returnsBAD_REQUESTforEach() throws IOException {
        // given
        List<String> ibans = readLinesFromFile("src/test/resources/list_of_invalid_IBANs.txt");

        for (String iban : ibans) {
            // when
            responseEntity = ibanValidatorService.validateIban(iban);

            // then
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), () -> "Failed for IBAN: " + iban);
        }
    }

    @Test
    void checkIban_withAnInvalidCountryCode_returnsBAD_REQUEST() {
        // given
        String iban = "ZE7280000810340009783242";

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
        String iban = "SE72800008103400097832422";

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
        String iban = "SE7280700810340009783242";

        // when
        responseEntity = ibanValidatorService.validateIban(iban);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(iban + "'s checkSum does not have a modulus of 1.",
                responseEntity.getBody());
    }

    // Helper method to read lines from a file
    private List<String> readLinesFromFile(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath));
    }
}
