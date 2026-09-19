package com.example.expensetracker.presentation.ui.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.presentation.adapter.CategoryAdapter;
import com.example.expensetracker.presentation.dialog.ConfirmDeleteDialog;
import com.example.expensetracker.presentation.ui.bottomsheet.AddEditCategoryBottomSheet;
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoriesFragment extends Fragment {

    private CategoryViewModel viewModel;
    private CategoryAdapter systemAdapter;
    private CategoryAdapter userAdapter;
    private RecyclerView recyclerSystemCategories;
    private RecyclerView recyclerUserCategories;
    private View rowSystemCategoriesHeader;
    private View rowUserCategoriesHeader;
    private TextView tvSystemCategoriesArrow;
    private TextView tvUserCategoriesArrow;
    private boolean systemCategoriesExpanded = false;
    private boolean userCategoriesExpanded = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerSystemCategories = view.findViewById(R.id.recyclerSystemCategories);
        recyclerUserCategories = view.findViewById(R.id.recyclerUserCategories);
        rowSystemCategoriesHeader = view.findViewById(R.id.rowSystemCategoriesHeader);
        rowUserCategoriesHeader = view.findViewById(R.id.rowUserCategoriesHeader);
        tvSystemCategoriesArrow = view.findViewById(R.id.tvSystemCategoriesArrow);
        tvUserCategoriesArrow = view.findViewById(R.id.tvUserCategoriesArrow);

        systemAdapter = new CategoryAdapter(
                requireContext(),
                this::onCategoryClick,
                this::onCategoryEdit,
                this::onCategoryDelete);

        userAdapter = new CategoryAdapter(
                requireContext(),
                this::onCategoryClick,
                this::onCategoryEdit,
                this::onCategoryDelete);

        recyclerSystemCategories.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        recyclerUserCategories.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        recyclerSystemCategories.setAdapter(systemAdapter);
        recyclerUserCategories.setAdapter(userAdapter);

        viewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        getParentFragmentManager().setFragmentResultListener(
                AddEditCategoryBottomSheet.CATEGORY_CHANGED_RESULT,
                getViewLifecycleOwner(),
                (requestKey, result) -> viewModel.loadCategories());

        observeViewModel();

        view.findViewById(R.id.btnAddCategory)
                .setOnClickListener(v -> showCreateCategory());

        rowSystemCategoriesHeader.setOnClickListener(
                v -> toggleSystemCategories());

        rowUserCategoriesHeader.setOnClickListener(
                v -> toggleUserCategories());

        setSystemCategoriesExpanded(false);
        setUserCategoriesExpanded(true);

        viewModel.loadCategories();
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case SUCCESS:
                            splitCategories(resource.getData());
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

        viewModel.getCategoryAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    if (resource.getStatus() == com.example.expensetracker.domain.common.Resource.Status.ERROR) {
                        Toast.makeText(
                                requireContext(),
                                resource.getMessage(),
                                Toast.LENGTH_LONG).show();
                        viewModel.resetCategoryAction();
                    }
                });

        viewModel.getDeleteAction().observe(
                getViewLifecycleOwner(),
                resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {
                        case SUCCESS:
                            Toast.makeText(
                                    requireContext(),
                                    "Đã xóa danh mục.",
                                    Toast.LENGTH_SHORT).show();

                            viewModel.resetDeleteAction();
                            viewModel.loadCategories();
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            viewModel.resetDeleteAction();
                            break;

                        case LOADING:
                        case IDLE:
                        default:
                            break;
                    }
                });
    }

    private void splitCategories(List<Category> categories) {
        List<Category> systemCategories = new ArrayList<>();
        List<Category> userCategories = new ArrayList<>();

        if (categories != null) {
            for (Category category : categories) {
                if (category.getUserId() == null) {
                    systemCategories.add(category);
                } else {
                    userCategories.add(category);
                }
            }
        }

        systemAdapter.setItems(systemCategories);
        userAdapter.setItems(userCategories);
    }

    private void toggleSystemCategories() {
        setSystemCategoriesExpanded(
                !systemCategoriesExpanded);
    }

    private void toggleUserCategories() {
        setUserCategoriesExpanded(
                !userCategoriesExpanded);
    }

    private void setSystemCategoriesExpanded(boolean expanded) {
        systemCategoriesExpanded = expanded;
        recyclerSystemCategories.setVisibility(
                expanded
                        ? View.VISIBLE
                        : View.GONE);
        tvSystemCategoriesArrow.setText(
                expanded
                        ? "⌃"
                        : "⌄");
    }

    private void setUserCategoriesExpanded(boolean expanded) {
        userCategoriesExpanded = expanded;
        recyclerUserCategories.setVisibility(
                expanded
                        ? View.VISIBLE
                        : View.GONE);
        tvUserCategoriesArrow.setText(
                expanded
                        ? "⌃"
                        : "⌄");
    }

    private void showCreateCategory() {
        viewModel.resetCategoryAction();

        AddEditCategoryBottomSheet sheet =
                AddEditCategoryBottomSheet.newCreateInstance();

        sheet.show(
                getParentFragmentManager(),
                "AddEditCategoryBottomSheet");
    }

    private void onCategoryClick(Category category) {
        if (category.getUserId() == null) {
            Toast.makeText(
                    requireContext(),
                    "Danh mục hệ thống chỉ được xem.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        onCategoryEdit(category);
    }

    private void onCategoryEdit(Category category) {
        if (category.getUserId() == null) {
            Toast.makeText(
                    requireContext(),
                    "Danh mục hệ thống không thể sửa.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.resetCategoryAction();

        AddEditCategoryBottomSheet sheet =
                AddEditCategoryBottomSheet.newEditInstance(
                        category);

        sheet.show(
                getParentFragmentManager(),
                "AddEditCategoryBottomSheet");
    }

    private void onCategoryDelete(Category category) {
        if (category.getUserId() == null) {
            Toast.makeText(
                    requireContext(),
                    "Danh mục hệ thống không thể xóa.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ConfirmDeleteDialog.show(
                requireContext(),
                "Xóa danh mục",
                "Bạn có chắc muốn xóa danh mục \"" +
                        category.getName() +
                        "\"?",
                () -> viewModel.deleteCategory(
                        category.getId()));
    }
}