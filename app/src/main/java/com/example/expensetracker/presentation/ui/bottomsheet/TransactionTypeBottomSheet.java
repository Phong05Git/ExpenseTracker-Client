package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class TransactionTypeBottomSheet extends BottomSheetDialogFragment {

    public interface OnTransactionTypeSelectedListener {
        void onTransactionTypeSelected(Integer type, String typeName);
    }

    private Integer selectedType;
    private OnTransactionTypeSelectedListener listener;

    private MaterialCardView cardIncome;
    private MaterialCardView cardExpense;
    private MaterialCardView cardAll;

    private TextView tvIncomeCheck;
    private TextView tvExpenseCheck;
    private TextView tvAllCheck;

    private ImageButton buttonClose;

    public static TransactionTypeBottomSheet newInstance(@Nullable Integer selectedType) {
        TransactionTypeBottomSheet sheet = new TransactionTypeBottomSheet();
        Bundle args = new Bundle();

        if (selectedType != null) {
            args.putInt("selectedType", selectedType);
        }

        sheet.setArguments(args);
        return sheet;
    }

    public void setOnTransactionTypeSelectedListener(OnTransactionTypeSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_transaction_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeData();
        setupListeners();
        updateSelection();
    }

    private void initializeViews(View view) {
        cardIncome = view.findViewById(R.id.cardTransactionTypeIncome);
        cardExpense = view.findViewById(R.id.cardTransactionTypeExpense);
        cardAll = view.findViewById(R.id.cardTransactionTypeAll);
        tvIncomeCheck = view.findViewById(R.id.tvTransactionTypeIncomeCheck);
        tvExpenseCheck = view.findViewById(R.id.tvTransactionTypeExpenseCheck);
        tvAllCheck = view.findViewById(R.id.tvTransactionTypeAllCheck);
        buttonClose = view.findViewById(R.id.buttonCloseTransactionType);
    }

    private void initializeData() {
        selectedType = null;

        if (getArguments() != null && getArguments().containsKey("selectedType")) {
            selectedType = getArguments().getInt("selectedType");
        }
    }

    private void setupListeners() {
        buttonClose.setOnClickListener(v -> dismiss());
        cardIncome.setOnClickListener(v -> selectType(1, "Thu nhập"));
        cardExpense.setOnClickListener(v -> selectType(2, "Chi tiêu"));
        cardAll.setOnClickListener(v -> selectType(null, "Tất cả"));
    }

    private void selectType(@Nullable Integer type, String typeName) {
        selectedType = type;

        if (listener != null) {
            listener.onTransactionTypeSelected(type, typeName);
        }

        dismiss();
    }

    private void updateSelection() {
        boolean incomeSelected = selectedType != null && selectedType == 1;
        boolean expenseSelected = selectedType != null && selectedType == 2;
        boolean allSelected = selectedType == null;

        updateCard(cardIncome, tvIncomeCheck, incomeSelected);
        updateCard(cardExpense, tvExpenseCheck, expenseSelected);
        updateCard(cardAll, tvAllCheck, allSelected);
    }

    private void updateCard(MaterialCardView card, TextView checkView, boolean selected) {
        if (selected) {
            card.setStrokeColor(requireContext().getColor(R.color.primary_green));
            checkView.setText("✓");
            checkView.setTextColor(requireContext().getColor(R.color.primary_green));
        } else {
            card.setStrokeColor(requireContext().getColor(R.color.light_text_secondary));
            checkView.setText("");
        }
    }
}