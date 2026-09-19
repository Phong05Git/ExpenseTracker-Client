package com.example.expensetracker.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.util.IconResolver;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public interface OnCategoryEditListener {
        void onCategoryEdit(Category category);
    }

    public interface OnCategoryDeleteListener {
        void onCategoryDelete(Category category);
    }

    private final Context context;
    private final List<Category> items = new ArrayList<>();
    private final OnCategoryClickListener clickListener;
    private final OnCategoryEditListener editListener;
    private final OnCategoryDeleteListener deleteListener;

    public CategoryAdapter(
            Context context,
            OnCategoryClickListener clickListener,
            OnCategoryEditListener editListener,
            OnCategoryDeleteListener deleteListener) {

        this.context = context;
        this.clickListener = clickListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void setItems(List<Category> categories) {
        items.clear();

        if (categories != null) {
            items.addAll(categories);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.item_category,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Category category = items.get(position);

        holder.ivIcon.setImageResource(
                IconResolver.getIconResId(category.getIcon())
        );

        try {
            int color = Color.parseColor(category.getColor());

            holder.ivIcon.setBackgroundTintList(
                    ColorStateList.valueOf(color)
            );

            holder.ivIcon.setColorFilter(Color.WHITE);

        } catch (Exception ignored) {

            holder.ivIcon.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(
                                    context,
                                    R.color.primary_green
                            )
                    )
            );

            holder.ivIcon.setColorFilter(Color.WHITE);
        }

        holder.tvName.setText(category.getName());
        holder.tvType.setText(
                category.isIncome()
                        ? "Thu nhập"
                        : "Chi tiêu"
        );

        boolean isSystemCategory =
                category.getUserId() == null;

        holder.tvSystem.setVisibility(
                isSystemCategory
                        ? View.VISIBLE
                        : View.GONE
        );

        holder.btnEdit.setVisibility(
                isSystemCategory
                        ? View.GONE
                        : View.VISIBLE
        );

        holder.btnDelete.setVisibility(
                isSystemCategory
                        ? View.GONE
                        : View.VISIBLE
        );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCategoryClick(category);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onCategoryEdit(category);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCategoryDelete(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvName;
        TextView tvType;
        TextView tvSystem;
        ImageButton btnEdit;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvType = itemView.findViewById(R.id.tvCategoryType);
            tvSystem = itemView.findViewById(R.id.tvCategorySystem);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}