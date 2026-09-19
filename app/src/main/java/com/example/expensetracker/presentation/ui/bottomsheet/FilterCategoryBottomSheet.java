package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.presentation.adapter.CategoryGridAdapter;
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterCategoryBottomSheet extends BottomSheetDialogFragment {

    public interface OnCategorySelectedListener {
        void onCategorySelected(@Nullable Integer categoryId, String categoryName);
    }

    private CategoryViewModel viewModel;
    private CategoryGridAdapter adapter;
    private OnCategorySelectedListener listener;

    private Integer selectedType;
    private Integer selectedCategoryId;

    public static FilterCategoryBottomSheet newInstance(@Nullable Integer selectedType, @Nullable Integer selectedCategoryId) {
        FilterCategoryBottomSheet sheet = new FilterCategoryBottomSheet();
        Bundle args = new Bundle();

        if (selectedType != null) {
            args.putInt("selectedType", selectedType);
        }

        if (selectedCategoryId != null) {
            args.putInt("selectedCategoryId", selectedCategoryId);
        }

        sheet.setArguments(args);
        return sheet;
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeData();
        initializeViews(view);
        setupRecyclerView(view);
        setupListeners(view);
        loadCategories();
    }

    private void initializeData() {
        selectedType = null;
        selectedCategoryId = null;

        if (getArguments() == null) {
            return;
        }

        if (getArguments().containsKey("selectedType")) {
            selectedType = getArguments().getInt("selectedType");
        }

        if (getArguments().containsKey("selectedCategoryId")) {
            selectedCategoryId = getArguments().getInt("selectedCategoryId");
        }
    }

    private void initializeViews(View view) {
        TextView tvTitle = view.findViewById(R.id.tvFilterCategoryTitle);
        tvTitle.setText("Chọn danh mục");

        MaterialButton buttonAll = view.findViewById(R.id.buttonFilterCategoryAll);
        buttonAll.setOnClickListener(v -> selectCategory(null, "Tất cả"));
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerCategories = view.findViewById(R.id.recyclerFilterCategories);

        adapter = new CategoryGridAdapter(
                requireContext(),
                category -> {
                    if (listener != null) {
                        listener.onCategorySelected(category.getId(), category.getName());
                    }

                    dismiss();
                });

        recyclerCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerCategories.setAdapter(adapter);
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.buttonCloseFilterCategory)
                .setOnClickListener(v -> dismiss());
    }

    private void loadCategories() {
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        viewModel.getCategories().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case SUCCESS:
                            List<Category> categories = resource.getData();
                            List<Category> filtered = new ArrayList<>();

                            if (categories != null) {
                                for (Category category : categories) {
                                    if (selectedType == null || category.getType() == selectedType) {
                                        filtered.add(category);
                                    }
                                }
                            }

                            adapter.setItems(filtered);
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case LOADING:
                        case IDLE:
                        default:
                            break;
                    }
                });

        viewModel.loadCategories();
    }

    private void selectCategory(@Nullable Integer categoryId, String categoryName) {
        selectedCategoryId = categoryId;

        if (listener != null) {
            listener.onCategorySelected(categoryId, categoryName);
        }

        dismiss();
    }
}