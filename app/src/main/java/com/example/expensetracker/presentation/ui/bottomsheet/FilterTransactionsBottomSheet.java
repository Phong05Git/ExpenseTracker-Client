package com.example.expensetracker.presentation.ui.bottomsheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.example.expensetracker.util.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterTransactionsBottomSheet extends BottomSheetDialogFragment {

    public interface OnFilterAppliedListener {
        void onFilterApplied(
                String fromDate,
                String toDate,
                Integer categoryId,
                Integer type,
                String keyword);
    }

    private TextView tvDateRange;
    private TextView tvTransactionType;
    private TextView tvCategory;

    private TextInputEditText editKeyword;

    private MaterialCardView cardDateRange;
    private MaterialCardView cardTransactionType;
    private MaterialCardView cardCategory;

    private Calendar fromCalendar;
    private Calendar toCalendar;

    private Integer selectedType;
    private Integer selectedCategoryId;
    private String selectedCategoryName;

    private OnFilterAppliedListener listener;

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeData();
        setupListeners();
        updateDateRangeText();
        updateTransactionTypeText();
        updateCategoryText();
    }

    private void initializeViews(View view) {
        tvDateRange = view.findViewById(R.id.tvFilterDateRange);
        tvTransactionType = view.findViewById(R.id.tvFilterTransactionType);
        tvCategory = view.findViewById(R.id.tvFilterCategory);

        editKeyword = view.findViewById(R.id.editFilterKeyword);

        cardDateRange = view.findViewById(R.id.cardFilterDateRange);
        cardTransactionType = view.findViewById(R.id.cardFilterTransactionType);
        cardCategory = view.findViewById(R.id.cardFilterCategory);

        ImageButton buttonClose = view.findViewById(R.id.buttonCloseFilterSheet);
        MaterialButton buttonClear = view.findViewById(R.id.buttonClearFilter);
        MaterialButton buttonApply = view.findViewById(R.id.buttonApplyFilter);

        buttonClose.setOnClickListener(v -> dismiss());
        buttonClear.setOnClickListener(v -> clearFilter());
        buttonApply.setOnClickListener(v -> applyFilter());
    }

    private void initializeData() {
        fromCalendar = null;
        toCalendar = null;
        selectedType = null;
        selectedCategoryId = null;
        selectedCategoryName = null;
    }

    private void setupListeners() {
        cardDateRange.setOnClickListener(v -> showDateRangePicker());
        cardTransactionType.setOnClickListener(v -> showTransactionTypePicker());
        cardCategory.setOnClickListener(v -> showCategoryPicker());
    }

    private void showDateRangePicker() {
        DateRangePickerBottomSheet sheet = DateRangePickerBottomSheet.newInstance(fromCalendar, toCalendar);

        sheet.setOnDateRangeConfirmedListener((startDate, endDate) -> {
            fromCalendar = (Calendar) startDate.clone();
            toCalendar = (Calendar) endDate.clone();
            updateDateRangeText();
        });

        sheet.show(getParentFragmentManager(), "FilterDateRangePickerBottomSheet");
    }

    private void showTransactionTypePicker() {
        TransactionTypeBottomSheet sheet = TransactionTypeBottomSheet.newInstance(selectedType);

        sheet.setOnTransactionTypeSelectedListener((type, typeName) -> {
            selectedType = type;

            selectedCategoryId = null;
            selectedCategoryName = null;

            updateTransactionTypeText();
            updateCategoryText();
        });

        sheet.show(getParentFragmentManager(), "FilterTransactionTypeBottomSheet");
    }

    private void showCategoryPicker() {
        FilterCategoryBottomSheet sheet = FilterCategoryBottomSheet.newInstance(selectedType, selectedCategoryId);

        sheet.setOnCategorySelectedListener((categoryId, categoryName) -> {
            selectedCategoryId = categoryId;
            selectedCategoryName = categoryName;
            updateCategoryText();
        });

        sheet.show(getParentFragmentManager(), "FilterCategoryBottomSheet");
    }

    @SuppressLint("SetTextI18n")
    private void updateDateRangeText() {
        if (fromCalendar == null || toCalendar == null) {
            tvDateRange.setText("Chọn khoảng thời gian");
            return;
        }

        tvDateRange.setText(DateUtils.formatDisplayDate(fromCalendar) + " - " + DateUtils.formatDisplayDate(toCalendar));
    }

    private void updateTransactionTypeText() {
        if (selectedType == null) {
            tvTransactionType.setText("Tất cả");
        } else if (selectedType == 1) {
            tvTransactionType.setText("Thu nhập");
        } else if (selectedType == 2) {
            tvTransactionType.setText("Chi tiêu");
        }
    }

    private void updateCategoryText() {
        if (selectedCategoryId == null || selectedCategoryName == null || selectedCategoryName.isBlank()) {
            tvCategory.setText("Tất cả");
            return;
        }

        tvCategory.setText(selectedCategoryName);
    }

    private void applyFilter() {
        String fromDate = null;
        String toDate = null;

        if (fromCalendar != null && toCalendar != null) {
            fromDate = DateUtils.formatApiDate(fromCalendar);

            Calendar exclusiveTo = (Calendar) toCalendar.clone();
            exclusiveTo.add(Calendar.DAY_OF_MONTH, 1);
            toDate = DateUtils.formatApiDate(exclusiveTo);
        }

        String keyword = null;

        if (editKeyword.getText() != null) {
            String value = editKeyword.getText().toString().trim();

            if (!value.isEmpty()) {
                keyword = value;
            }
        }

        if (listener != null) {
            listener.onFilterApplied(
                    fromDate,
                    toDate,
                    selectedCategoryId,
                    selectedType,
                    keyword);
        }

        dismiss();
    }

    private void clearFilter() {
        fromCalendar = null;
        toCalendar = null;
        selectedType = null;
        selectedCategoryId = null;
        selectedCategoryName = null;

        editKeyword.setText("");

        updateDateRangeText();
        updateTransactionTypeText();
        updateCategoryText();

        if (listener != null) {
            listener.onFilterApplied(null, null, null, null, null);
        }

        dismiss();
    }
}