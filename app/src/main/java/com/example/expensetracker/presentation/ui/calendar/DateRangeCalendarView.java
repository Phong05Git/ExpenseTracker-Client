package com.example.expensetracker.presentation.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;

import java.util.Calendar;

public class DateRangeCalendarView extends View {

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    private static final String[] WEEK_DAYS = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int primaryGreen;
    private final int textPrimary;
    private final int textSecondary;
    private final int lightSurface;

    private final Calendar displayedMonth = Calendar.getInstance();
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();

    private boolean hasStartDate;
    private boolean hasEndDate;

    private float cellWidth;
    private float cellHeight;

    private OnDateSelectedListener listener;

    public DateRangeCalendarView(Context context) {
        super(context);
        primaryGreen = ContextCompat.getColor(context, R.color.primary_green);
        textPrimary = ContextCompat.getColor(context, R.color.light_text_primary);
        textSecondary = ContextCompat.getColor(context, R.color.light_text_secondary);
        lightSurface = ContextCompat.getColor(context, R.color.light_surface);
        setClickable(true);
    }

    public DateRangeCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        primaryGreen = ContextCompat.getColor(context, R.color.primary_green);
        textPrimary = ContextCompat.getColor(context, R.color.light_text_primary);
        textSecondary = ContextCompat.getColor(context, R.color.light_text_secondary);
        lightSurface = ContextCompat.getColor(context, R.color.light_surface);
        setClickable(true);
    }

    public DateRangeCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        primaryGreen = ContextCompat.getColor(context, R.color.primary_green);
        textPrimary = ContextCompat.getColor(context, R.color.light_text_primary);
        textSecondary = ContextCompat.getColor(context, R.color.light_text_secondary);
        lightSurface = ContextCompat.getColor(context, R.color.light_surface);
        setClickable(true);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void setMonth(Calendar month) {
        displayedMonth.set(
                month.get(Calendar.YEAR),
                month.get(Calendar.MONTH),
                1);
        normalizeTime(displayedMonth);
        invalidate();
    }

    public Calendar getMonth() {
        return (Calendar) displayedMonth.clone();
    }

    public void setRange(@Nullable Calendar start, @Nullable Calendar end) {
        hasStartDate = start != null;
        hasEndDate = end != null;

        if (start != null) {
            startDate.setTimeInMillis(start.getTimeInMillis());
            normalizeTime(startDate);
        }

        if (end != null) {
            endDate.setTimeInMillis(end.getTimeInMillis());
            normalizeTime(endDate);
        }

        invalidate();
    }

    @Nullable
    public Calendar getStartDate() {
        if (!hasStartDate) {
            return null;
        }

        return (Calendar) startDate.clone();
    }

    @Nullable
    public Calendar getEndDate() {
        if (!hasEndDate) {
            return null;
        }

        return (Calendar) endDate.clone();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float weekHeaderHeight = dp(28, density);
        float calendarHeight = getHeight() - weekHeaderHeight;

        cellWidth = getWidth() / 7f;
        cellHeight = calendarHeight / 6f;

        drawWeekDays(canvas, weekHeaderHeight, density);
        drawDays(canvas, weekHeaderHeight, density);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        float density = getResources().getDisplayMetrics().density;
        float weekHeaderHeight = dp(28, density);

        if (event.getY() < weekHeaderHeight) {
            return true;
        }

        int column = (int) (event.getX() / cellWidth);
        int row = (int) ((event.getY() - weekHeaderHeight) / cellHeight);

        if (column < 0 || column > 6 || row < 0 || row > 5) {
            return true;
        }

        Calendar date = getDateFromPosition(row, column);

        if (listener != null) {
            listener.onDateSelected(date);
        }

        return true;
    }

    private void drawWeekDays(Canvas canvas, float height, float density) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp(12, density));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(textSecondary);

        float baseline = height / 2f - (paint.ascent() + paint.descent()) / 2f;

        for (int i = 0; i < 7; i++) {
            float x = i * cellWidth + cellWidth / 2f;
            canvas.drawText(WEEK_DAYS[i], x, baseline, paint);
        }
    }

    private void drawDays(Canvas canvas, float top, float density) {
        Calendar firstDay = (Calendar) displayedMonth.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        int mondayOffset = firstDayOfWeek == Calendar.SUNDAY
                ? 6
                : firstDayOfWeek - Calendar.MONDAY;

        for (int position = 0; position < 42; position++) {
            Calendar date = (Calendar) firstDay.clone();
            date.add(Calendar.DAY_OF_MONTH, position - mondayOffset);
            normalizeTime(date);

            int row = position / 7;
            int column = position % 7;

            float centerX = column * cellWidth + cellWidth / 2f;
            float centerY = top + row * cellHeight + cellHeight * 0.42f;

            drawDay(canvas, date, centerX, centerY, density);
        }
    }

    private void drawDay(Canvas canvas, Calendar date, float centerX, float centerY, float density) {
        boolean currentMonth =
                date.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH) &&
                        date.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR);

        boolean isStart =
                hasStartDate &&
                        isSameDate(date, startDate);

        boolean isEnd =
                hasEndDate &&
                        isSameDate(date, endDate);

        boolean isInsideRange =
                hasStartDate &&
                        hasEndDate &&
                        !date.before(startDate) &&
                        !date.after(endDate);

        if (isInsideRange) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(withAlpha(primaryGreen, 45));

            float left = centerX - cellWidth / 2f;
            float right = centerX + cellWidth / 2f;
            float top = centerY - cellHeight * 0.30f;
            float bottom = centerY + cellHeight * 0.30f;

            canvas.drawRect(left, top, right, bottom, paint);
        }

        if (isStart || isEnd) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(primaryGreen);
            canvas.drawCircle(centerX, centerY, dp(17, density), paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(dp(14, density));
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT,
                isStart || isEnd
                        ? Typeface.BOLD
                        : Typeface.NORMAL));

        if (isStart || isEnd) {
            paint.setColor(lightSurface);
        } else if (!currentMonth) {
            paint.setColor(textSecondary);
        } else {
            paint.setColor(textPrimary);
        }

        float textBaseline =
                centerY - (paint.ascent() + paint.descent()) / 2f;

        canvas.drawText(
                String.valueOf(date.get(Calendar.DAY_OF_MONTH)),
                centerX,
                textBaseline,
                paint);
    }

    private Calendar getDateFromPosition(int row, int column) {
        Calendar firstDay = (Calendar) displayedMonth.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        int mondayOffset = firstDayOfWeek == Calendar.SUNDAY
                ? 6
                : firstDayOfWeek - Calendar.MONDAY;

        int position = row * 7 + column;

        Calendar date = (Calendar) firstDay.clone();
        date.add(Calendar.DAY_OF_MONTH, position - mondayOffset);
        normalizeTime(date);

        return date;
    }

    private boolean isSameDate(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private void normalizeTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private int withAlpha(int color, int alpha) {
        return android.graphics.Color.argb(
                alpha,
                android.graphics.Color.red(color),
                android.graphics.Color.green(color),
                android.graphics.Color.blue(color));
    }

    private float dp(float value, float density) {
        return value * density;
    }
}