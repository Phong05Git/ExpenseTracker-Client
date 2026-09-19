package com.example.expensetracker.util;

public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    public static String formatAmount(double amount) {
        String value = String.valueOf(Math.round(amount));
        return formatAmountString(value);
    }

    public static String formatAmountString(String value) {
        String digits = value
                .replace(".", "")
                .replace(",", "")
                .trim();

        if (digits.isEmpty()) {
            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        int length = digits.length();

        for (int i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) {
                builder.append('.');
            }

            builder.append(
                    digits.charAt(i));
        }

        return builder.toString();
    }

    public static double parseAmount(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        String digits = value
                .replace(".", "")
                .replace(",", "")
                .trim();

        if (digits.isEmpty()) {
            return 0;
        }

        return Double.parseDouble(digits);
    }
}