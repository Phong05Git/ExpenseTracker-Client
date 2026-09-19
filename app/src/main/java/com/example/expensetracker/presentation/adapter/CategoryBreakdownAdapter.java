package com.example.expensetracker.presentation.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.CategoryBreakdown;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryBreakdownAdapter {

    private final Context context;
    private final List<CategoryBreakdown> items =
            new ArrayList<>();

    public CategoryBreakdownAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<CategoryBreakdown> data) {
        items.clear();

        if (data != null) {
            items.addAll(data);
        }
    }

    public void bind(LinearLayout container) {
        LayoutInflater inflater =
                LayoutInflater.from(context);

        for (CategoryBreakdown item : items) {
            LinearLayout row =
                    (LinearLayout) inflater.inflate(
                            R.layout.item_category_breakdown,
                            container,
                            false);

            TextView name =
                    row.findViewById(R.id.tvCategoryName);

            TextView amount =
                    row.findViewById(R.id.tvCategoryAmount);

            TextView percentage =
                    row.findViewById(
                            R.id.tvCategoryPercentage);

            ProgressBar progress =
                    row.findViewById(
                            R.id.categoryProgress);

            name.setText(item.getCategoryName());

            amount.setText(
                    formatMoney(item.getAmount()));

            percentage.setText(
                    String.format(
                            Locale.US,
                            "%.2f%%",
                            item.getPercentage()));

            progress.setMax(100);

            progress.setProgress(
                    (int) Math.round(
                            item.getPercentage()));

            int color =
                    getCategoryColor(
                            item.getCategoryColor());

            progress.setProgressTintList(
                    ColorStateList.valueOf(color));

            container.addView(row);
        }
    }

    private int getCategoryColor(String categoryColor) {
        if (categoryColor != null
                && !categoryColor.isBlank()) {

            try {
                return Color.parseColor(
                        categoryColor);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ContextCompat.getColor(
                context,
                R.color.primary_green);
    }

    private String formatMoney(double amount) {
        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        new Locale("vi", "VN"));

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount) + " ₫";
    }
}