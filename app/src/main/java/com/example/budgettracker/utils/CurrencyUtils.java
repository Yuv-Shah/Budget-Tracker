package com.example.budgettracker.utils;

import java.util.Locale;

public class CurrencyUtils {

    /**
     * Formats a double amount into Indian Rupee format with Lakhs and Crores comma placements.
     * Example: 100000 -> "₹1,00,000.00"
     */
    public static String formatRupee(double amount) {
        return formatRupeeInternal(amount, true);
    }

    /**
     * Formats a double amount into Indian Rupee format without decimal places.
     * Example: 100000 -> "₹1,00,000"
     */
    public static String formatRupeeNoDecimals(double amount) {
        return formatRupeeInternal(amount, false);
    }

    private static String formatRupeeInternal(double amount, boolean showDecimals) {
        boolean isNegative = amount < 0;
        double absAmount = Math.abs(amount);

        long integerPart = (long) absAmount;
        double fractionalPart = absAmount - integerPart;

        String intStr = String.valueOf(integerPart);
        StringBuilder sb = new StringBuilder();

        int len = intStr.length();
        if (len <= 3) {
            sb.append(intStr);
        } else {
            String lastThree = intStr.substring(len - 3);
            String remaining = intStr.substring(0, len - 3);

            StringBuilder remSb = new StringBuilder();
            int remLen = remaining.length();
            for (int i = 0; i < remLen; i++) {
                if (i > 0 && (remLen - i) % 2 == 0) {
                    remSb.append(",");
                }
                remSb.append(remaining.charAt(i));
            }
            sb.append(remSb).append(",").append(lastThree);
        }

        if (showDecimals) {
            int decimals = (int) Math.round(fractionalPart * 100);
            if (decimals >= 100) {
                // Handle edge case where rounding pushes fractional part to 100
                decimals = 0;
            }
            sb.append(String.format(Locale.US, ".%02d", decimals));
        }

        String result = sb.toString();
        return (isNegative ? "-₹" : "₹") + result;
    }
}
