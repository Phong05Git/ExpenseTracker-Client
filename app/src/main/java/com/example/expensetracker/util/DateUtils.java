package com.example.expensetracker.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {
    }

    public static String formatApiDate(
            Calendar calendar) {

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US);

        return formatter.format(
                calendar.getTime());
    }

    public static String formatDisplayDate(
            Calendar calendar) {

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        new Locale("vi", "VN"));

        return formatter.format(
                calendar.getTime());
    }

    public static Calendar parseApiDate(
            String date) {

        Calendar calendar =
                Calendar.getInstance();

        if (date == null ||
                date.length() < 10) {
            return calendar;
        }

        try {
            String value =
                    date.substring(0, 10);

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US);

            calendar.setTime(
                    formatter.parse(value));

        } catch (Exception ignored) {
        }

        return calendar;
    }
}