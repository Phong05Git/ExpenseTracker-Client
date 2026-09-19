package com.example.expensetracker.presentation.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Transaction;

import java.util.Calendar;
import java.util.List;

public class TransactionCalendarView extends View {

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    private final Calendar displayedMonth = Calendar.getInstance();
    private final Calendar selectedDate = Calendar.getInstance();
    private final Calendar today = Calendar.getInstance();

    private final TransactionCalendarRenderer renderer;

    private OnDateSelectedListener listener;

    public TransactionCalendarView(Context context) {
        super(context);
        renderer = createRenderer(context);
        setClickable(true);
    }

    public TransactionCalendarView(
            Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        renderer = createRenderer(context);
        setClickable(true);
    }

    public TransactionCalendarView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        renderer = createRenderer(context);
        setClickable(true);
    }

    private TransactionCalendarRenderer createRenderer(Context context) {
        return new TransactionCalendarRenderer(
                ContextCompat.getColor(
                        context,
                        R.color.primary_green),
                ContextCompat.getColor(
                        context,
                        R.color.income_blue),
                ContextCompat.getColor(
                        context,
                        R.color.expense_red),
                ContextCompat.getColor(
                        context,
                        R.color.light_text_primary),
                ContextCompat.getColor(
                        context,
                        R.color.light_text_secondary),
                ContextCompat.getColor(
                        context,
                        R.color.light_surface));
    }

    public void setOnDateSelectedListener(
            OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedDate(Calendar date) {
        selectedDate.setTimeInMillis(
                date.getTimeInMillis());

        displayedMonth.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                1);

        invalidate();
    }

    public void setMonth(Calendar month) {
        displayedMonth.set(
                month.get(Calendar.YEAR),
                month.get(Calendar.MONTH),
                1);

        invalidate();
    }

    public void setTransactions(List<Transaction> transactions) {
        renderer.setTransactions(transactions);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        renderer.draw(
                canvas,
                getWidth(),
                getHeight(),
                displayedMonth,
                selectedDate,
                today,
                getResources()
                        .getDisplayMetrics()
                        .density);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        float density = getResources()
                        .getDisplayMetrics()
                        .density;

        if (!renderer.isInsideCalendar(
                event.getY(),
                density)) {
            return true;
        }

        int column = renderer.getColumn(event.getX());
        int row = renderer.getRow(event.getY(), density);

        if (column < 0 ||
                column > 6 ||
                row < 0 ||
                row > 5) {
            return true;
        }

        Calendar firstDay = (Calendar) displayedMonth.clone();

        firstDay.set(
                Calendar.DAY_OF_MONTH,
                1);

        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);

        int mondayOffset = firstDayOfWeek == Calendar.SUNDAY
                        ? 6
                        : firstDayOfWeek - Calendar.MONDAY;

        int position = row * 7 + column;

        Calendar clickedDate = (Calendar) firstDay.clone();

        clickedDate.add(
                Calendar.DAY_OF_MONTH,
                position - mondayOffset);

        selectedDate.setTimeInMillis(clickedDate.getTimeInMillis());

        invalidate();

        if (listener != null) {
            listener.onDateSelected((Calendar) selectedDate.clone());
        }

        return true;
    }
}