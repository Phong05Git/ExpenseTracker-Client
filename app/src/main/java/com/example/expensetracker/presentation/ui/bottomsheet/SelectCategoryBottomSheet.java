package com.example.expensetracker.presentation.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.presentation.adapter.CategoryGridAdapter;
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoryBottomSheet extends BottomSheetDialogFragment {

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    private CategoryViewModel viewModel;
    private OnCategorySelectedListener listener;
    private int selectedType;
    private RecyclerView recyclerCategories;
    private TextView tvTitle;
    private CategoryGridAdapter adapter;

    public static SelectCategoryBottomSheet newInstance(int type) {
        SelectCategoryBottomSheet sheet = new SelectCategoryBottomSheet();
        Bundle args = new Bundle();
        args.putInt("type", type);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.bottom_sheet_select_category,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        selectedType = getArguments() != null
                ? getArguments().getInt("type", 2)
                : 2;

        viewModel =
                new ViewModelProvider(requireActivity())
                        .get(CategoryViewModel.class);

        tvTitle = view.findViewById(R.id.tvSelectCategoryTitle);
        recyclerCategories =
                view.findViewById(R.id.recyclerSelectCategories);

        tvTitle.setText(
                selectedType == 1
                        ? "Chọn danh mục thu nhập"
                        : "Chọn danh mục chi tiêu"
        );

        adapter = new CategoryGridAdapter(
                requireContext(),
                category -> {
                    if (listener != null) {
                        listener.onCategorySelected(category);
                    }

                    dismiss();
                }
        );

        recyclerCategories.setLayoutManager(
                new GridLayoutManager(requireContext(), 3)
        );

        recyclerCategories.setAdapter(adapter);

        view.findViewById(R.id.buttonCloseSelectCategory)
                .setOnClickListener(v -> dismiss());

        viewModel.getCategories().observe(
                getViewLifecycleOwner(),
                resource -> {

                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {

                        case SUCCESS:

                            List<Category> categories =
                                    resource.getData();

                            List<Category> filtered =
                                    new ArrayList<>();

                            if (categories != null) {
                                for (Category category : categories) {
                                    if (category.getType() == selectedType) {
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
                                    Toast.LENGTH_LONG
                            ).show();

                            break;

                        case LOADING:
                        case IDLE:
                        default:
                            break;
                    }
                });

        viewModel.loadCategories();
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "SelectCategoryBottomSheet");
    }
}