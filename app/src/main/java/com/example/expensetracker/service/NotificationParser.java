package com.example.expensetracker.service;

import android.app.Notification;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NotificationParser {
    private static final String MB_PACKAGE = "com.mbmobile";
    private static final String TIMO_PACKAGE = "io.lifestyle.plus";
    private static final String BIDV_PACKAGE = "com.vnpay.bidv";

    private static final int INCOME_TYPE = 1;
    private static final int EXPENSE_TYPE = 2;

    private static final Pattern MB_PATTERN = Pattern.compile(
            "GD:\\s*([+-])\\s*([\\d,.]+)VND\\s+(\\d{2}/\\d{2}/\\d{2})\\s+([\\d:]+).*?\\|ND:\\s*(.+)$",
            Pattern.DOTALL);

    private static final Pattern TIMO_PATTERN = Pattern.compile(
            "Số dư tài khoản vừa\\s+(tăng|giảm)\\s+([\\d.,]+)\\s+vào\\s+(\\d{2}/\\d{2}/\\d{4})\\s+([\\d:]+).*?Mô tả:\\s*(.+?)(?:\\s+Số tài khoản:|$)",
            Pattern.DOTALL);

    private static final Pattern BIDV_PATTERN = Pattern.compile(
            "Thời gian giao dịch:\\s*(\\d{2}:\\d{2})\\s+(\\d{2}/\\d{2}/\\d{4}).*?Số tiền GD:\\s*([+-])\\s*([\\d,.]+)\\s*VND.*?Nội dung giao dịch:\\s*(.+?)(?:\\s+Mã giao dịch:|$)",
            Pattern.DOTALL);

    private NotificationParser() {
    }

    public static NotificationTransaction parse(
            String packageName,
            Notification notification) {
        if (!isSupportedPackage(packageName) || notification == null) {
            return null;
        }

        String text = extractText(notification);

        if (text == null || text.isBlank()) {
            return null;
        }

        if (MB_PACKAGE.equals(packageName)) {
            return parseMbBank(text);
        }

        if (TIMO_PACKAGE.equals(packageName)) {
            return parseTimo(text);
        }

        if (BIDV_PACKAGE.equals(packageName)) {
            return parseBidv(text);
        }

        return null;
    }

    private static boolean isSupportedPackage(String packageName) {
        return MB_PACKAGE.equals(packageName) ||
                TIMO_PACKAGE.equals(packageName) ||
                BIDV_PACKAGE.equals(packageName);
    }

    private static String extractText(Notification notification) {
        Bundle extras = notification.extras;

        if (extras == null) {
            return null;
        }

        CharSequence bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);

        if (bigText != null && !bigText.toString().isBlank()) {
            return normalizeText(bigText.toString());
        }

        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

        if (text != null && !text.toString().isBlank()) {
            return normalizeText(text.toString());
        }

        return null;
    }

    private static NotificationTransaction parseMbBank(String text) {
        Matcher matcher = MB_PATTERN.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String sign = matcher.group(1);
        String amountText = matcher.group(2);
        String date = matcher.group(3);
        String time = matcher.group(4);
        String note = matcher.group(5);

        Double amount = parseAmount(amountText);

        if (amount == null) {
            return null;
        }

        int type = "+".equals(sign) ? INCOME_TYPE : EXPENSE_TYPE;
        String transactionDate = parseMbDate(date, time);

        if (transactionDate == null) {
            return null;
        }

        return new NotificationTransaction(
                amount,
                type,
                cleanNote(note),
                transactionDate,
                "MB Bank");
    }

    private static NotificationTransaction parseTimo(String text) {
        Matcher matcher = TIMO_PATTERN.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String direction = matcher.group(1);
        String amountText = matcher.group(2);
        String date = matcher.group(3);
        String time = matcher.group(4);
        String note = matcher.group(5);

        Double amount = parseAmount(amountText);

        if (amount == null) {
            return null;
        }

        int type = "tăng".equalsIgnoreCase(direction) ? INCOME_TYPE : EXPENSE_TYPE;
        String transactionDate = parseDate(
                date + " " + time,
                "dd/MM/yyyy HH:mm");

        if (transactionDate == null) {
            return null;
        }

        return new NotificationTransaction(
                amount,
                type,
                cleanNote(note),
                transactionDate,
                "Timo");
    }

    private static NotificationTransaction parseBidv(String text) {
        Matcher matcher = BIDV_PATTERN.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String time = matcher.group(1);
        String date = matcher.group(2);
        String sign = matcher.group(3);
        String amountText = matcher.group(4);
        String note = matcher.group(5);

        Double amount = parseAmount(amountText);

        if (amount == null) {
            return null;
        }

        int type = "+".equals(sign) ? INCOME_TYPE : EXPENSE_TYPE;
        String transactionDate = parseDate(
                time + " " + date,
                "HH:mm dd/MM/yyyy");

        if (transactionDate == null) {
            return null;
        }

        return new NotificationTransaction(
                amount,
                type,
                cleanNote(note),
                transactionDate,
                "BIDV");
    }

    private static Double parseAmount(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.replace(",", "").replace(".", "");

        try {
            double amount = Double.parseDouble(normalized);

            if (amount < 1000) {
                return null;
            }

            return amount;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String parseMbDate(String date, String time) {
        return parseDate(
                date + " " + time,
                "dd/MM/yy HH:mm");
    }

    private static String parseDate(
            String value,
            String pattern) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(
                    pattern,
                    Locale.getDefault());

            inputFormat.setLenient(false);

            Date parsedDate = inputFormat.parse(value);

            if (parsedDate == null) {
                return null;
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US);

            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String cleanNote(String note) {
        if (note == null) {
            return "";
        }

        return note.trim().replaceAll("\\s+", " ");
    }

    private static String normalizeText(String text) {
        return text.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
    }
}