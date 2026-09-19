package com.example.expensetracker.presentation.ui.bottomsheet;

import android.app.DatePickerDialog;
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

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.presentation.dialog.ConfirmDeleteDialog;
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel;
import com.example.expensetracker.util.CurrencyUtils;
import com.example.expensetracker.util.DateUtils;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEditTransactionBottomSheet extends BottomSheetDialogFragment {

    public static final String TRANSACTION_CHANGED_RESULT = "transaction_changed";
    public static final String TRANSACTION_CHANGED_BUNDLE = "changed";

    private TransactionViewModel viewModel;
    private Transaction transaction;

    private TextView tvTitle;
    private TextView tvExpense;
    private TextView tvIncome;
    private TextView tvCategory;
    private TextView tvDate;
    private TextInputEditText editAmount;
    private TextInputEditText editNote;
    private MaterialButton buttonSave;
    private MaterialButton buttonDelete;

    private int selectedCategoryId = -1;
    private String selectedCategoryName = "";
    private int selectedType = 2;
    private Calendar selectedDate;
    private boolean isFormattingAmount;

    public static AddEditTransactionBottomSheet newCreateInstance(TransactionViewModel viewModel) {
        AddEditTransactionBottomSheet sheet = new AddEditTransactionBottomSheet();
        sheet.viewModel = viewModel;
        return sheet;
    }

    public static AddEditTransactionBottomSheet newEditInstance(TransactionViewModel viewModel, Transaction transaction) {
        AddEditTransactionBottomSheet sheet = new AddEditTransactionBottomSheet();
        sheet.viewModel = viewModel;
        sheet.transaction = transaction;
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_edit_transaction, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeData();
        setupAmountFormatter();
        setupListeners(view);
        observeActions();
    }

    private void initializeViews(View view) {
        tvTitle = view.findViewById(R.id.tvTransactionSheetTitle);
        tvExpense = view.findViewById(R.id.tvTransactionExpense);
        tvIncome = view.findViewById(R.id.tvTransactionIncome);
        tvCategory = view.findViewById(R.id.tvTransactionCategory);
        tvDate = view.findViewById(R.id.tvTransactionDate);
        editAmount = view.findViewById(R.id.editTransactionAmount);
        editNote = view.findViewById(R.id.editTransactionNote);
        buttonSave = view.findViewById(R.id.buttonSaveTransaction);
        buttonDelete = view.findViewById(R.id.buttonDeleteTransaction);

        editAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void initializeData() {
        selectedDate = Calendar.getInstance();

        if (transaction == null) {
            tvTitle.setText("Thêm giao dịch");
            selectedType = 2;
            selectedCategoryId = -1;
            selectedCategoryName = "";
            buttonDelete.setVisibility(View.GONE);
        } else {
            tvTitle.setText("Sửa giao dịch");
            buttonDelete.setVisibility(View.VISIBLE);
            selectedCategoryId = transaction.getCategoryId();
            selectedCategoryName = transaction.getCategoryName();
            selectedType = transaction.getType();
            editAmount.setText(CurrencyUtils.formatAmount(transaction.getAmount()));

            if (editAmount.getText() != null) {
                editAmount.setSelection(editAmount.getText().length());
            }

            if (transaction.getNote() != null) {
                editNote.setText(transaction.getNote());
            }

            selectedDate = DateUtils.parseApiDate(transaction.getTransactionDate());
        }

        updateTypeSelection();
        updateCategoryText();
        updateDateText();
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
        tvExpense.setOnClickListener(v -> {
            if (selectedType != 2) {
                selectedType = 2;
                selectedCategoryId = -1;
                selectedCategoryName = "";
                updateTypeSelection();
                updateCategoryText();
                tvCategory.setError(null);
            }
        });

        tvIncome.setOnClickListener(v -> {
            if (selectedType != 1) {
                selectedType = 1;
                selectedCategoryId = -1;
                selectedCategoryName = "";
                updateTypeSelection();
                updateCategoryText();
                tvCategory.setError(null);
            }
        });

        tvCategory.setOnClickListener(v -> showCategoryPicker());
        tvDate.setOnClickListener(v -> showDatePicker());

        view.findViewById(R.id.buttonCloseTransactionSheet)
                .setOnClickListener(v -> dismiss());

        buttonSave.setOnClickListener(v -> saveTransaction());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void showCategoryPicker() {
        SelectCategoryBottomSheet sheet = SelectCategoryBottomSheet.newInstance(selectedType);

        sheet.setOnCategorySelectedListener(category -> {
            selectedCategoryId = category.getId();
            selectedCategoryName = category.getName();
            updateCategoryText();
            tvCategory.setError(null);
        });

        sheet.show(getParentFragmentManager(), "SelectCategoryBottomSheet");
    }

    private void observeActions() {
        viewModel.getTransactionAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case LOADING:
                            setActionButtonsEnabled(false);
                            break;

                        case SUCCESS:
                            setActionButtonsEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    transaction == null
                                            ? "Đã thêm giao dịch."
                                            : "Đã cập nhật giao dịch.",
                                    Toast.LENGTH_SHORT).show();

                            sendTransactionChangedResult();
                            viewModel.resetTransactionAction();
                            dismiss();
                            break;

                        case ERROR:
                            setActionButtonsEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    (transaction == null
                                            ? "Không thể thêm giao dịch. "
                                            : "Không thể cập nhật giao dịch. ")
                                            + resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case IDLE:
                        default:
                            setActionButtonsEnabled(true);
                            break;
                    }
                });

        viewModel.getDeleteAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case LOADING:
                            setActionButtonsEnabled(false);
                            break;

                        case SUCCESS:
                            setActionButtonsEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    "Đã xóa giao dịch.",
                                    Toast.LENGTH_SHORT).show();

                            sendTransactionChangedResult();
                            viewModel.resetDeleteAction();
                            dismiss();
                            break;

                        case ERROR:
                            setActionButtonsEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case IDLE:
                        default:
                            setActionButtonsEnabled(true);
                            break;
                    }
                });
    }

    private void sendTransactionChangedResult() {
        Bundle result = new Bundle();
        result.putBoolean(TRANSACTION_CHANGED_BUNDLE, true);

        getParentFragmentManager().setFragmentResult(
                TRANSACTION_CHANGED_RESULT,
                result);
    }

    private void setActionButtonsEnabled(boolean enabled) {
        buttonSave.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    private void saveTransaction() {
        String amountText = getText(editAmount);

        if (!validateAmount()) {
            editAmount.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidCategory(selectedCategoryId)) {
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
            editAmount.setError(ValidationUtils.amountError(amount, "Số tiền"));
            editAmount.requestFocus();
            return;
        }

        String note = getText(editNote);
        String date = DateUtils.formatApiDate(selectedDate);

        if (transaction == null) {
            viewModel.createTransaction(
                    selectedCategoryId,
                    amount,
                    selectedType,
                    note,
                    date,
                    null);
            return;
        }

        viewModel.updateTransaction(
                transaction.getId(),
                selectedCategoryId,
                amount,
                selectedType,
                note,
                date);
    }

    private boolean validateAmount() {
        String amountText = getText(editAmount);
        if (ValidationUtils.isBlank(amountText)) {
            editAmount.setError("Vui lòng nhập số tiền.");
            return false;
        }

        try {
            double amount = CurrencyUtils.parseAmount(amountText);
            if (!ValidationUtils.isValidAmount(amount)) {
                editAmount.setError(ValidationUtils.amountError(amount, "Số tiền"));
                return false;
            }
        } catch (NumberFormatException exception) {
            editAmount.setError("Số tiền không hợp lệ.");
            return false;
        }

        editAmount.setError(null);
        return true;
    }

    private void confirmDelete() {
        if (transaction == null) {
            return;
        }

        ConfirmDeleteDialog.show(
                requireContext(),
                "Xóa giao dịch",
                "Bạn có chắc chắn muốn xóa giao dịch này?",
                this::deleteTransaction);
    }

    private void deleteTransaction() {
        viewModel.deleteTransaction(transaction.getId());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void updateTypeSelection() {
        if (selectedType == 1) {
            tvIncome.setBackgroundResource(
                    R.drawable.bg_transaction_type_income_selected);

            tvExpense.setBackgroundResource(
                    R.drawable.bg_transaction_type_unselected);

            tvIncome.setTextColor(
                    requireContext().getColor(R.color.primary_green));

            tvExpense.setTextColor(
                    requireContext().getColor(R.color.light_text_secondary));

            return;
        }

        tvExpense.setBackgroundResource(
                R.drawable.bg_transaction_type_expense_selected);

        tvIncome.setBackgroundResource(
                R.drawable.bg_transaction_type_unselected);

        tvExpense.setTextColor(
                requireContext().getColor(R.color.expense_red));

        tvIncome.setTextColor(
                requireContext().getColor(R.color.light_text_secondary));
    }

    private void updateCategoryText() {
        if (selectedCategoryName == null || selectedCategoryName.isBlank()) {
            tvCategory.setText("Chọn danh mục");
            tvCategory.setTextColor(
                    requireContext().getColor(R.color.light_text_secondary));
            return;
        }

        tvCategory.setText(selectedCategoryName);
        tvCategory.setTextColor(
                requireContext().getColor(R.color.light_text_primary));
    }

    private void updateDateText() {
        tvDate.setText(DateUtils.formatDisplayDate(selectedDate));
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}
