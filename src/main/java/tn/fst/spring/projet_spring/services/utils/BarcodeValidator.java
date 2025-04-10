package tn.fst.spring.projet_spring.services.utils;

import org.springframework.stereotype.Component;

@Component
public class BarcodeValidator {
    public boolean isValidTunisianBarcode(String barcode) {
        if (barcode == null || barcode.length() != 13) {
            return false;
        }

        // Vérification que le code commence par 619 (code pays Tunisie)
        if (!barcode.startsWith("619")) {
            return false;
        }

        // Vérification du checksum (dernier chiffre)
        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(barcode.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3;
            }
            int checksum = (10 - (sum % 10)) % 10;
            int lastDigit = Character.getNumericValue(barcode.charAt(12));

            return checksum == lastDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}