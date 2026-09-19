package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class MonthPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnMonthSelectedListener {
        void onMonthSelected(int month, int year);
    }

    private OnMonthSelectedListener listener;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private int selectedMonth;
    private int selectedYear;

    public static MonthPickerBottomSheet newInstance(int month, int year) {
        MonthPickerBottomSheet sheet = new MonthPickerBottomSheet();
        Bundle args = new Bundle();
        args.putInt("month", month);
        args.putInt("year", year);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_month_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar now = Calendar.getInstance();

        selectedMonth = now.get(Calendar.MONTH) + 1;
        selectedYear = now.get(Calendar.YEAR);

        if (getArguments() != null) {
            int argumentMonth = getArguments().getInt("month", selectedMonth);
            int argumentYear = getArguments().getInt("year", selectedYear);

            if (argumentMonth >= 1 && argumentMonth <= 12) {
                selectedMonth = argumentMonth;
            }

            if (argumentYear >= 1900 && argumentYear <= 2100) {
                selectedYear = argumentYear;
            }
        }

        monthPicker = view.findViewById(R.id.monthPicker);
        yearPicker = view.findViewById(R.id.yearPicker);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(selectedMonth);

        yearPicker.setMinValue(now.get(Calendar.YEAR) - 5);
        yearPicker.setMaxValue(now.get(Calendar.YEAR) + 5);
        yearPicker.setValue(selectedYear);

        view.findViewById(R.id.buttonCloseMonthPicker).setOnClickListener(v -> dismiss());

        MaterialButton buttonSelect = view.findViewById(R.id.buttonSelectMonth);

        buttonSelect.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMonthSelected(monthPicker.getValue(), yearPicker.getValue());
            }

            dismiss();
        });
    }
}