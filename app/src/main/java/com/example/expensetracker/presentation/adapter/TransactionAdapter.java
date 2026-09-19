package com.example.expensetracker.presentation.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.util.IconResolver;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    private final Context context;
    private final List<Transaction> items = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final OnTransactionClickListener listener;

    public TransactionAdapter(Context context, OnTransactionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<Transaction> transactions) {
        items.clear();

        if (transactions != null) {
            items.addAll(transactions);
        }

        notifyDataSetChanged();
    }

    public void setCategories(List<Category> categories) {
        this.categories.clear();

        if (categories != null) {
            this.categories.addAll(categories);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_transaction,
                parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = items.get(position);

        String categoryIcon = transaction.getCategoryIcon();
        String categoryColor = transaction.getCategoryColor();

        if (categoryIcon != null && !categoryIcon.isBlank()) {
            holder.ivCategoryIcon.setImageResource(
                    IconResolver.getIconResId(categoryIcon));
        } else {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_tag);
        }

        int parsedColor = parseColor(categoryColor);

        holder.ivCategoryIcon.setBackgroundTintList(
                ColorStateList.valueOf(parsedColor));
        holder.ivCategoryIcon.setColorFilter(Color.WHITE);

        holder.tvCategoryName.setText(transaction.getCategoryName());

        String note = transaction.getNote();

        if (note == null || note.isBlank()) {
            holder.tvNote.setVisibility(View.GONE);
        } else {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(note);
        }

        String source = transaction.getSource();

        if (source == null || source.isBlank()) {
            holder.tvSource.setVisibility(View.GONE);
        } else {
            holder.tvSource.setVisibility(View.VISIBLE);
            holder.tvSource.setText(source);
        }

        String prefix = transaction.isIncome() ? "+ " : "- ";

        holder.tvAmount.setText(
                prefix + formatMoney(transaction.getAmount()));

        holder.tvAmount.setTextColor(
                ContextCompat.getColor(
                        context,
                        transaction.isIncome()
                                ? R.color.income_blue
                                : R.color.expense_red));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private Category findCategory(int categoryId) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }

        return null;
    }

    private int parseColor(String value) {
        try {
            return Color.parseColor(value);
        } catch (Exception ignored) {
            return ContextCompat.getColor(
                    context,
                    R.color.primary_green);
        }
    }

    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(
                new Locale("vi", "VN"));

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount) + " ₫";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        TextView tvNote;
        TextView tvSource;
        TextView tvAmount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvNote = itemView.findViewById(R.id.tvTransactionNote);
            tvSource = itemView.findViewById(R.id.tvTransactionSource);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}