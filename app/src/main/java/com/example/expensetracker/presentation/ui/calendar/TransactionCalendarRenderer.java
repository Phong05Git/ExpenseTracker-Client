package com.example.expensetracker.presentation.ui.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.example.expensetracker.domain.model.Transaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TransactionCalendarRenderer {

    private static final String[] WEEK_DAYS = {
            "T2", "T3", "T4", "T5", "T6", "T7", "CN"
    };

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Map<String, DateTransactionInfo> transactionInfoMap = new HashMap<>();

    private final int primaryGreen;
    private final int incomeBlue;
    private final int expenseRed;
    private final int textPrimary;
    private final int textSecondary;
    private final int lightSurface;

    private float cellWidth;
    private float cellHeight;

    public TransactionCalendarRenderer(
            int primaryGreen,
            int incomeBlue,
            int expenseRed,
            int textPrimary,
            int textSecondary,
            int lightSurface) {

        this.primaryGreen = primaryGreen;
        this.incomeBlue = incomeBlue;
        this.expenseRed = expenseRed;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.lightSurface = lightSurface;
    }

    public void setTransactions(List<Transaction> transactions) {
        transactionInfoMap.clear();

        if (transactions == null) {
            return;
        }

        for (Transaction transaction : transactions) {
            String date = normalizeDate(transaction.getTransactionDate());

            if (date.isEmpty()) {
                continue;
            }

            DateTransactionInfo info = transactionInfoMap.get(date);

            if (info == null) {
                info = new DateTransactionInfo();
                transactionInfoMap.put(date, info);
            }

            if (transaction.isIncome()) {
                info.hasIncome = true;
            } else {
                info.hasExpense = true;
            }
        }
    }

    public void draw(
            @NonNull Canvas canvas,
            float width,
            float height,
            Calendar displayedMonth,
            Calendar selectedDate,
            Calendar today,
            float density) {

        float weekHeaderHeight = dp(28, density);
        float calendarHeight = height - weekHeaderHeight;

        cellWidth = width / 7f;
        cellHeight = calendarHeight / 6f;

        drawWeekDays(canvas, weekHeaderHeight, density);
        drawDays(
                canvas,
                weekHeaderHeight,
                displayedMonth,
                selectedDate,
                today,
                density);
    }

    public int getColumn(float x) {
        return (int) (x / cellWidth);
    }

    public int getRow(float y, float density) {
        float weekHeaderHeight = dp(28, density);
        return (int) ((y - weekHeaderHeight) / cellHeight);
    }

    public boolean isInsideCalendar(float y, float density) {
        return y >= dp(28, density);
    }

    private void drawWeekDays(
            Canvas canvas,
            float height,
            float density) {

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp(12, density));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD));
        paint.setColor(textSecondary);

        float baseline = height / 2f -
                (paint.ascent() + paint.descent()) / 2f;

        for (int i = 0; i < 7; i++) {
            float x = i * cellWidth + cellWidth / 2f;

            canvas.drawText(
                    WEEK_DAYS[i],
                    x,
                    baseline,
                    paint);
        }
    }

    private void drawDays(
            Canvas canvas,
            float top,
            Calendar displayedMonth,
            Calendar selectedDate,
            Calendar today,
            float density) {

        Calendar firstDay = (Calendar) displayedMonth.clone();

        firstDay.set(
                Calendar.DAY_OF_MONTH,
                1);

        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);

        int mondayOffset =
                firstDayOfWeek == Calendar.SUNDAY
                        ? 6
                        : firstDayOfWeek - Calendar.MONDAY;

        for (int position = 0; position < 42; position++) {
            Calendar date = (Calendar) firstDay.clone();

            date.add(
                    Calendar.DAY_OF_MONTH,
                    position - mondayOffset);

            int row = position / 7;
            int column = position % 7;

            float centerX =
                    column * cellWidth +
                            cellWidth / 2f;

            float centerY =
                    top +
                            row * cellHeight +
                            cellHeight * 0.42f;

            drawDay(
                    canvas,
                    date,
                    centerX,
                    centerY,
                    displayedMonth,
                    selectedDate,
                    today,
                    density);
        }
    }

    private void drawDay(
            Canvas canvas,
            Calendar date,
            float centerX,
            float centerY,
            Calendar displayedMonth,
            Calendar selectedDate,
            Calendar today,
            float density) {

        boolean currentMonth =
                date.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH) &&
                        date.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR);

        boolean isToday = isSameDate(date, today);
        boolean isSelected = isSameDate(date, selectedDate);

        String key = createDateKey(date);
        DateTransactionInfo info = transactionInfoMap.get(key);

        if (isToday) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(primaryGreen);

            canvas.drawCircle(
                    centerX,
                    centerY,
                    dp(17, density),
                    paint);
        }

        if (isSelected) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2, density));
            paint.setColor(primaryGreen);

            canvas.drawCircle(
                    centerX,
                    centerY,
                    dp(17, density),
                    paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(14, density));
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                isToday || isSelected
                        ? Typeface.BOLD
                        : Typeface.NORMAL));

        if (isToday) {
            paint.setColor(lightSurface);
        } else if (isSelected) {
            paint.setColor(primaryGreen);
        } else if (!currentMonth) {
            paint.setColor(textSecondary);
        } else {
            paint.setColor(textPrimary);
        }

        float textBaseline =
                centerY -
                        (paint.ascent() +
                                paint.descent()) / 2f;

        canvas.drawText(
                String.valueOf(
                        date.get(Calendar.DAY_OF_MONTH)),
                centerX,
                textBaseline,
                paint);

        if (info == null) {
            return;
        }

        float dotY = centerY + dp(22, density);

        if (info.hasIncome && info.hasExpense) {
            drawDot(
                    canvas,
                    centerX - dp(4, density),
                    dotY,
                    incomeBlue,
                    density);

            drawDot(
                    canvas,
                    centerX + dp(4, density),
                    dotY,
                    expenseRed,
                    density);
        } else if (info.hasIncome) {
            drawDot(
                    canvas,
                    centerX,
                    dotY,
                    incomeBlue,
                    density);
        } else if (info.hasExpense) {
            drawDot(
                    canvas,
                    centerX,
                    dotY,
                    expenseRed,
                    density);
        }
    }

    private void drawDot(
            Canvas canvas,
            float x,
            float y,
            int color,
            float density) {

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        canvas.drawCircle(
                x,
                y,
                dp(3.5f, density),
                paint);
    }

    private boolean isSameDate(
            Calendar first,
            Calendar second) {

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private String normalizeDate(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        if (value.length() >= 10) {
            return value.substring(0, 10);
        }

        return value;
    }

    private String createDateKey(Calendar date) {
        return String.format(
                Locale.US,
                "%04d-%02d-%02d",
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH));
    }

    private float dp(float value, float density) {
        return value * density;
    }

    private static class DateTransactionInfo {
        boolean hasIncome;
        boolean hasExpense;
    }
}