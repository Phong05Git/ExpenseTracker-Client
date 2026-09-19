package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel;
import com.example.expensetracker.util.CurrencyUtils;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditBudgetBottomSheet extends BottomSheetDialogFragment {

    public static final String BUDGET_CHANGED_RESULT = "budget_changed";
    public static final String BUDGET_CHANGED_BUNDLE = "changed";

    private BudgetViewModel budgetViewModel;
    private Budget budget;

    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvMonth;
    private TextInputEditText editAmount;
    private MaterialButton buttonSave;
    private boolean isFormattingAmount;

    private int selectedCategoryId = -1;
    private String selectedCategoryName = "";
    private int selectedMonth;
    private int selectedYear;

    public static AddEditBudgetBottomSheet newCreateInstance() {
        return new AddEditBudgetBottomSheet();
    }

    public static AddEditBudgetBottomSheet newEditInstance(Budget budget) {
        AddEditBudgetBottomSheet sheet = new AddEditBudgetBottomSheet();
        sheet.budget = budget;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_edit_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        initializeViews(view);
        initializeData();
        setupAmountFormatter();
        setupListeners(view);
        observeAction();
    }

    private void initializeViews(View view) {
        tvTitle = view.findViewById(R.id.tvBudgetSheetTitle);
        tvCategory = view.findViewById(R.id.tvBudgetCategory);
        tvMonth = view.findViewById(R.id.tvBudgetMonth);
        editAmount = view.findViewById(R.id.editBudgetAmount);
        buttonSave = view.findViewById(R.id.buttonSaveBudget);

        editAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void initializeData() {
        Calendar now = Calendar.getInstance();
        selectedMonth = now.get(Calendar.MONTH) + 1;
        selectedYear = now.get(Calendar.YEAR);

        if (budget == null) {
            tvTitle.setText("Thêm ngân sách");
            selectedCategoryId = -1;
            selectedCategoryName = "";
        } else {
            tvTitle.setText("Sửa ngân sách");
            selectedCategoryId = budget.getCategoryId();
            selectedCategoryName = budget.getCategoryName();
            selectedMonth = budget.getMonth();
            selectedYear = budget.getYear();

            editAmount.setText(
                    CurrencyUtils.formatAmount(
                            budget.getLimitAmount()));

            if (editAmount.getText() != null) {
                editAmount.setSelection(
                        editAmount.getText().length());
            }
        }

        updateCategoryText();
        updateMonthText();
    }

    private void setupAmountFormatter() {
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isFormattingAmount) {
                    return;
                }

                String digits = editable.toString().replace(".", "").replace(",", "");

                if (digits.isEmpty()) {
                    if (editAmount.hasFocus()) validateAmount();
                    return;
                }

                isFormattingAmount = true;
                String formatted = CurrencyUtils.formatAmountString(digits);
                editAmount.setText(formatted);
                editAmount.setSelection(formatted.length());
                isFormattingAmount = false;
                if (editAmount.hasFocus()) validateAmount();
            }
        });
    }

    private void setupListeners(View view) {
        tvCategory.setOnClickListener(v -> showCategoryPicker());
        tvMonth.setOnClickListener(v -> showMonthPicker());

        view.findViewById(R.id.buttonCloseBudgetSheet)
                .setOnClickListener(v -> dismiss());

        buttonSave.setOnClickListener(v -> saveBudget());
    }

    private void showCategoryPicker() {
        SelectCategoryBottomSheet sheet =
                SelectCategoryBottomSheet.newInstance(2);

        sheet.setOnCategorySelectedListener(category -> {
            selectedCategoryId = category.getId();
            selectedCategoryName = category.getName();
            updateCategoryText();
            tvCategory.setError(null);
        });

        sheet.show(
                getParentFragmentManager(),
                "SelectCategoryBottomSheet");
    }

    private void showMonthPicker() {
        MonthPickerBottomSheet sheet =
                MonthPickerBottomSheet.newInstance(
                        selectedMonth,
                        selectedYear);

        sheet.setOnMonthSelectedListener((month, year) -> {
            selectedMonth = month;
            selectedYear = year;
            updateMonthText();
        });

        sheet.show(
                getParentFragmentManager(),
                "MonthPickerBottomSheet");
    }

    private void observeAction() {
        budgetViewModel.getBudgetAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case LOADING:
                            buttonSave.setEnabled(false);
                            break;

                        case SUCCESS:
                            buttonSave.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    budget == null
                                            ? "Đã thêm ngân sách."
                                            : "Đã cập nhật ngân sách.",
                                    Toast.LENGTH_SHORT).show();

                            sendBudgetChangedResult();
                            budgetViewModel.resetBudgetAction();
                            dismiss();
                            break;

                        case ERROR:
                            buttonSave.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    (budget == null
                                            ? "Không thể thêm ngân sách. "
                                            : "Không thể cập nhật ngân sách. ")
                                            + resource.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            budgetViewModel.resetBudgetAction();
                            break;

                        case IDLE:
                        default:
                            buttonSave.setEnabled(true);
                            break;
                    }
                });
    }

    private void sendBudgetChangedResult() {
        Bundle result = new Bundle();
        result.putBoolean(BUDGET_CHANGED_BUNDLE, true);

        getParentFragmentManager().setFragmentResult(
                BUDGET_CHANGED_RESULT,
                result);
    }

    private void saveBudget() {
        String amountText = getText(editAmount);

        if (!validateAmount()) {
            editAmount.requestFocus();
            return;
        }

        if (selectedCategoryId <= 0) {
            tvCategory.setError("Vui lòng chọn danh mục.");
            return;
        }

        double amount;

        try {
            amount = CurrencyUtils.parseAmount(amountText);
        } catch (NumberFormatException e) {
            editAmount.setError("Số tiền không hợp lệ.");
            editAmount.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidAmount(amount)) {
            editAmount.setError(ValidationUtils.amountError(amount, "Hạn mức"));
            editAmount.requestFocus();
            return;
        }

        if (budget == null) {
            budgetViewModel.createBudget(
                    selectedCategoryId,
                    amount,
                    selectedMonth,
                    selectedYear);
            return;
        }

        budgetViewModel.updateBudget(
                budget.getId(),
                selectedCategoryId,
                amount,
                selectedMonth,
                selectedYear);
    }

    private boolean validateAmount() {
        String amountText = getText(editAmount);
        if (amountText.isBlank()) {
            editAmount.setError("Vui lòng nhập hạn mức.");
            return false;
        }

        try {
            double amount = CurrencyUtils.parseAmount(amountText);
            if (!ValidationUtils.isValidAmount(amount)) {
                editAmount.setError(ValidationUtils.amountError(amount, "Hạn mức"));
                return false;
            }
        } catch (NumberFormatException exception) {
            editAmount.setError("Số tiền không hợp lệ.");
            return false;
        }

        editAmount.setError(null);
        return true;
    }

    private void updateCategoryText() {
        if (selectedCategoryName == null
                || selectedCategoryName.isBlank()) {

            tvCategory.setText("Chọn danh mục");
            tvCategory.setTextColor(
                    requireContext().getColor(
                            R.color.light_text_secondary));
            return;
        }

        tvCategory.setText(selectedCategoryName);
        tvCategory.setTextColor(
                requireContext().getColor(
                        R.color.light_text_primary));
    }

    private void updateMonthText() {
        tvMonth.setText(
                "Tháng " + selectedMonth + "/" + selectedYear);
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}
