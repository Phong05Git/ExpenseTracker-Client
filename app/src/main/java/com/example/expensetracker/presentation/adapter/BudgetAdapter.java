package com.example.expensetracker.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.util.IconResolver;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter {

    public interface OnBudgetEditListener {
        void onBudgetEdit(Budget budget);
    }

    public interface OnBudgetDeleteListener {
        void onBudgetDelete(Budget budget);
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final ViewGroup container;
    private final OnBudgetEditListener editListener;
    private final OnBudgetDeleteListener deleteListener;

    public BudgetAdapter(Context context, ViewGroup container, OnBudgetEditListener editListener, OnBudgetDeleteListener deleteListener) {
        this.context = context;
        this.container = container;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(List<Budget> budgets) {
        container.removeAllViews();

        if (budgets == null || budgets.isEmpty()) {
            return;
        }

        for (Budget budget : budgets) {
            addBudgetItem(budget);
        }
    }

    private void addBudgetItem(Budget budget) {
        View itemView = inflater.inflate(R.layout.item_budget, container, false);

        ImageView ivBudgetIcon = itemView.findViewById(R.id.ivBudgetIcon);
        TextView tvBudgetCategoryName = itemView.findViewById(R.id.tvBudgetCategoryName);
        TextView tvBudgetAmount = itemView.findViewById(R.id.tvBudgetAmount);
        TextView tvBudgetPeriod = itemView.findViewById(R.id.tvBudgetPeriod);
        TextView tvBudgetRemaining = itemView.findViewById(R.id.tvBudgetRemaining);
        View btnEditBudget = itemView.findViewById(R.id.btnEditBudget);
        View btnDeleteBudget = itemView.findViewById(R.id.btnDeleteBudget);

        tvBudgetCategoryName.setText(budget.getCategoryName());
        tvBudgetAmount.setText(formatMoney(budget.getLimitAmount()));
        tvBudgetPeriod.setText("Đã chi: " + formatMoney(budget.getSpentAmount()));
        tvBudgetRemaining.setText(getRemainingText(budget));
        tvBudgetRemaining.setTextColor(ContextCompat.getColor(context, getRemainingColor(budget)));

        String categoryIcon = budget.getCategoryIcon();
        String categoryColor = budget.getCategoryColor();

        if (categoryIcon != null && !categoryIcon.isBlank()) {
            ivBudgetIcon.setImageResource(IconResolver.getIconResId(categoryIcon));
        } else {
            ivBudgetIcon.setImageResource(R.drawable.ic_tag);
        }

        if (categoryColor != null && !categoryColor.isBlank()) {
            try {
                ivBudgetIcon.setColorFilter(Color.parseColor(categoryColor));
            } catch (IllegalArgumentException e) {
                ivBudgetIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_green));
            }
        } else {
            ivBudgetIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_green));
        }

        btnEditBudget.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onBudgetEdit(budget);
            }
        });

        btnDeleteBudget.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onBudgetDelete(budget);
            }
        });

        container.addView(itemView);
    }

    private String getRemainingText(Budget budget) {
        double remaining = budget.getRemainingAmount();

        if (remaining < 0) {
            return "Vượt: " + formatMoney(Math.abs(remaining));
        }

        if (remaining == 0) {
            return "Đã hết ngân sách";
        }

        return "Còn lại: " + formatMoney(remaining);
    }

    private int getRemainingColor(Budget budget) {
        double remaining = budget.getRemainingAmount();

        if (remaining < 0) {
            return R.color.expense_red;
        }

        if (remaining == 0) {
            return R.color.budget_warning_orange;
        }

        if (remaining <= budget.getLimitAmount() * 0.2) {
            return R.color.budget_warning_orange;
        }

        return R.color.primary_green;
    }

    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " ₫";
    }
}