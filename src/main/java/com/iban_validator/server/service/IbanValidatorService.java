package com.iban_validator.server.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IbanValidatorService {

    private final List<String> countryCodesAndLength = List.of("AL-28", "AD-24", "AT-20", "AZ-28", "BH-22", "BY-28", "BE-16",
            "BA-20", "BR-29", "BG-22", "CR-22", "HR-21", "CY-28", "CZ-24", "DK-18", "DO-28", "EG-29", "SV-28", "EE-20", "FO-18",
            "FI-18", "FR-27", "GE-22", "DE-22", "GI-23", "GR-27", "GL-18", "GT-28", "VA-22", "HU-28", "IS-26", "IQ-23", "IE-22",
            "IL-23", "IT-27", "JO-30", "KZ-20", "XK-20", "KW-30", "LV-21", "LB-28", "LY-25", "LI-21", "LT-20", "LU-20", "MT-31",
            "MR-27", "MU-30", "MD-24", "MC-27", "ME-22", "NL-18", "MK-19", "NO-15", "PK-24", "PS-29", "PL-28", "PT-25", "QA-29",
            "RO-24", "LC-32", "SM-27", "ST-25", "SA-24", "RS-22", "SC-31", "SK-24", "SI-19", "ES-24", "SD-18", "SE-24", "CH-21",
            "TL-23", "TN-24", "TR-26", "UA-29", "AE-23", "GB-22", "VG-24", "DZ-26", "AO-25", "BJ-28", "BF-28", "BI-16", "CM-27",
            "CV-25", "CF-27", "TD-27", "KM-27", "CG-27", "DJ-27", "GQ-27", "GA-27", "GW-25", "HN-28", "IR-26", "CI-28", "MG-27",
            "ML-28", "MA-28", "MZ-25", "NI-32", "NE-28", "SN-28", "TG-28");

    private boolean ibanCountryCodeCheck;
    private boolean ibanLengthCheck;

    public ResponseEntity<String> validateIban(String iban) {
        checkIbanCountryCodeAndLength(iban);
        if (ibanCountryCodeCheck)
            ibanCountryCodeCheck = false;
        else
            return ResponseEntity.badRequest().body(iban.substring(0, 2) + " is not a valid IBAN country code.");
        if (ibanLengthCheck)
            ibanLengthCheck = false;
        else
            return ResponseEntity.badRequest().body(iban + " with country code " + iban.substring(0, 2) +
                    " does not have a valid length.");
        if (!checkForModulusOfOne(iban))
            return ResponseEntity.badRequest().body(iban + "'s checkSum does not have a modulus of 1.");
        return ResponseEntity.ok().body(iban + " is a valid IBAN.");
    }

    private void checkIbanCountryCodeAndLength(String iban) {
        String countryCode = iban.substring(0, 2);
        ibanCountryCodeCheck = false;
        ibanLengthCheck = false;

        for (String countryCodeAndLength : countryCodesAndLength) {
            String validCountryCode = countryCodeAndLength.substring(0, 2);
            int expectedLength = Integer.parseInt(countryCodeAndLength.substring(3));

            if (countryCode.equals(validCountryCode)) {
                ibanCountryCodeCheck = true;
                ibanLengthCheck = iban.length() == expectedLength;
                break;  // Stop once a valid country code is found and length is checked
            }
        }
    }

    private boolean checkForModulusOfOne(String iban) {
        String convertedIban = convertAllAlphaCharactersToNumericCharacters(
                iban.substring(4).toUpperCase() + iban.substring(0, 4).toUpperCase());
        int modulus;
        while (true) {
            if (convertedIban.length() > 9) {
                modulus = Integer.parseInt(convertedIban.substring(0, 9)) % 97;
                convertedIban = modulus + convertedIban.substring(9);
            } else {
                modulus = Integer.parseInt(convertedIban) % 97;
                break;
            }
        }
        return modulus == 1;
    }

    private String convertAllAlphaCharactersToNumericCharacters(String iban) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(iban);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String replacement = String.valueOf(matcher.group().charAt(0) - 'A' + 10);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
