package com.example.bankcards.util;

public class MaskedCardNumber {

    public static String maskCardNumber(String fullNumber) {
        if (fullNumber.length() != 16) {
            throw new IllegalArgumentException("Некорректный номер карты");
        }
        String last4 = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
