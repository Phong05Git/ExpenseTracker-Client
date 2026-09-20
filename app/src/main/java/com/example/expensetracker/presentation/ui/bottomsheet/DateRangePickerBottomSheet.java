package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.example.expensetracker.presentation.ui.calendar.DateRangeCalendarView;
import com.example.expensetracker.util.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateRangePickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnDateRangeConfirmedListener {
        void onDateRangeConfirmed(Calendar startDate, Calendar endDate);
    }

    private TextView tvCurrentMonth;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private MaterialButton buttonCancel;
    private MaterialButton buttonConfirm;
    private DateRangeCalendarView calendarView;

    private Calendar displayedMonth;
    private Calendar selectedStartDate;
    private Calendar selectedEndDate;

    private OnDateRangeConfirmedListener confirmedListener;

    public static DateRangePickerBottomSheet newInstance(@Nullable Calendar initialStartDate, @Nullable Calendar initialEndDate) {
        DateRangePickerBottomSheet sheet = new DateRangePickerBottomSheet();
        Bundle args = new Bundle();

        if (initialStartDate != null) {
            args.putString("startDate", DateUtils.formatApiDate(initialStartDate));
        }

        if (initialEndDate != null) {
            args.putString("endDate", DateUtils.formatApiDate(initialEndDate));
        }

        sheet.setArguments(args);
        return sheet;
    }

    public void setOnDateRangeConfirmedListener(OnDateRangeConfirmedListener listener) {
        confirmedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_date_range_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeData();
        setupListeners();
        updateHeader();
        updateDateFields();
        updateCalendar();
    }

    private void initializeViews(View view) {
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        buttonCancel = view.findViewById(R.id.buttonCancelDateRange);
        buttonConfirm = view.findViewById(R.id.buttonConfirmDateRange);
        calendarView = view.findViewById(R.id.dateRangeCalendar);
    }

    private void initializeData() {
        Calendar now = Calendar.getInstance();

        displayedMonth = Calendar.getInstance();
        displayedMonth.set(Calendar.YEAR, now.get(Calendar.YEAR));
        displayedMonth.set(Calendar.MONTH, now.get(Calendar.MONTH));
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
        normalizeTime(displayedMonth);

        selectedStartDate = null;
        selectedEndDate = null;

        if (getArguments() == null) {
            return;
        }

        String startDateString = getArguments().getString("startDate");
        String endDateString = getArguments().getString("endDate");

        if (startDateString != null && !startDateString.isBlank()) {
            Calendar parsedStartDate = DateUtils.parseApiDate(startDateString);
            selectedStartDate = parsedStartDate;
            normalizeTime(selectedStartDate);

            displayedMonth.set(
                    selectedStartDate.get(Calendar.YEAR),
                    selectedStartDate.get(Calendar.MONTH),
                    1);

            normalizeTime(displayedMonth);
        }

        if (endDateString != null && !endDateString.isBlank()) {
            Calendar parsedEndDate = DateUtils.parseApiDate(endDateString);
            selectedEndDate = parsedEndDate;
            normalizeTime(selectedEndDate);
        }

        if (selectedStartDate != null && selectedEndDate != null && !selectedEndDate.after(selectedStartDate)) {
            selectedEndDate = null;
        }
    }

    private void setupListeners() {
        calendarView.setOnDateSelectedListener(this::onDateSelected);

        tvCurrentMonth.setOnClickListener(v -> showMonthPicker());

        buttonCancel.setOnClickListener(v -> dismiss());
        buttonConfirm.setOnClickListener(v -> confirmSelection());
    }

    private void onDateSelected(Calendar date) {
        Calendar clickedDate = (Calendar) date.clone();
        normalizeTime(clickedDate);

        if (selectedStartDate == null) {
            selectedStartDate = clickedDate;
            selectedEndDate = null;
            updateDateFields();
            updateCalendar();
            return;
        }

        if (selectedEndDate == null) {
            if (clickedDate.after(selectedStartDate)) {
                selectedEndDate = clickedDate;
            } else {
                selectedStartDate = clickedDate;
                selectedEndDate = null;
            }

            updateDateFields();
            updateCalendar();
            return;
        }

        selectedStartDate = clickedDate;
        selectedEndDate = null;

        updateDateFields();
        updateCalendar();
    }

    private void showMonthPicker() {
        MonthPickerBottomSheet sheet = MonthPickerBottomSheet.newInstance(
                displayedMonth.get(Calendar.MONTH) + 1,
                displayedMonth.get(Calendar.YEAR));

        sheet.setOnMonthSelectedListener((month, year) -> {
            displayedMonth.set(Calendar.YEAR, year);
            displayedMonth.set(Calendar.MONTH, month - 1);
            displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
            normalizeTime(displayedMonth);

            updateHeader();
            updateCalendar();
        });

        sheet.show(getParentFragmentManager(), "DateRangeMonthPickerBottomSheet");
    }

    private void confirmSelection() {
        if (selectedStartDate == null || selectedEndDate == null) {
            Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn đủ ngày bắt đầu và ngày kết thúc.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!selectedEndDate.after(selectedStartDate)) {
            Toast.makeText(
                    requireContext(),
                    "Ngày kết thúc phải sau ngày bắt đầu.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (confirmedListener != null) {
            confirmedListener.onDateRangeConfirmed(
                    (Calendar) selectedStartDate.clone(),
                    (Calendar) selectedEndDate.clone());
        }

        dismiss();
    }

    private void updateHeader() {
        SimpleDateFormat monthFormat = new SimpleDateFormat(
                "MMMM",
                new Locale("vi", "VN"));

        String month = monthFormat.format(displayedMonth.getTime());

        if (!month.isEmpty()) {
            month = month.substring(0, 1).toUpperCase(Locale.getDefault()) + month.substring(1);
        }

        tvCurrentMonth.setText(month + " " + displayedMonth.get(Calendar.YEAR));
    }

    private void updateDateFields() {
        if (selectedStartDate == null) {
            tvStartDate.setText("___");
        } else {
            tvStartDate.setText(DateUtils.formatDisplayDate(selectedStartDate));
        }

        if (selectedEndDate == null) {
            tvEndDate.setText("___");
        } else {
            tvEndDate.setText(DateUtils.formatDisplayDate(selectedEndDate));
        }
    }

    private void updateCalendar() {
        calendarView.setMonth(displayedMonth);
        calendarView.setRange(selectedStartDate, selectedEndDate);
    }

    private void normalizeTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}