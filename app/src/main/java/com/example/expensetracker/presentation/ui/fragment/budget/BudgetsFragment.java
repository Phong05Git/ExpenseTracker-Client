package com.example.expensetracker.presentation.ui.fragment.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.presentation.adapter.BudgetAdapter;
import com.example.expensetracker.presentation.dialog.ConfirmDeleteDialog;
import com.example.expensetracker.presentation.ui.bottomsheet.AddEditBudgetBottomSheet;
import com.example.expensetracker.presentation.ui.bottomsheet.AddEditTransactionBottomSheet;
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class BudgetsFragment extends Fragment {

    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;

    private LinearLayout budgetList;
    private ProgressBar progressBudgets;
    private TextView tvEmptyBudgets;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_budgets,
                container,
                false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        budgetList = view.findViewById(R.id.budgetList);
        progressBudgets = view.findViewById(R.id.progressBudgets);
        tvEmptyBudgets = view.findViewById(R.id.tvEmptyBudgets);

        budgetViewModel = new ViewModelProvider(requireActivity())
                .get(BudgetViewModel.class);

        budgetAdapter = new BudgetAdapter(
                requireContext(),
                budgetList,
                this::showEditBudget,
                this::showDeleteBudget);

        getParentFragmentManager().setFragmentResultListener(
                AddEditBudgetBottomSheet.BUDGET_CHANGED_RESULT,
                getViewLifecycleOwner(),
                (requestKey, result) -> budgetViewModel.loadBudgets());

        getParentFragmentManager().setFragmentResultListener(
                AddEditTransactionBottomSheet.TRANSACTION_CHANGED_RESULT,
                getViewLifecycleOwner(),
                (requestKey, result) -> budgetViewModel.loadBudgets());

        observeBudget();
        observeBudgetActions();

        view.findViewById(R.id.btnAddBudget)
                .setOnClickListener(v -> showCreateBudget());

        budgetViewModel.loadBudgets();
    }

    private void observeBudget() {

        budgetViewModel.getBudgets().observe(
                getViewLifecycleOwner(),
                resource -> {

                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {

                        case LOADING:
                            progressBudgets.setVisibility(View.VISIBLE);
                            tvEmptyBudgets.setVisibility(View.GONE);
                            break;

                        case SUCCESS:
                            progressBudgets.setVisibility(View.GONE);

                            budgetAdapter.setItems(resource.getData());

                            boolean isEmpty = resource.getData() == null
                                    || resource.getData().isEmpty();

                            tvEmptyBudgets.setVisibility(
                                    isEmpty ? View.VISIBLE : View.GONE);

                            break;

                        case ERROR:
                            progressBudgets.setVisibility(View.GONE);
                            tvEmptyBudgets.setVisibility(View.GONE);

                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case IDLE:
                        default:
                            progressBudgets.setVisibility(View.GONE);
                            break;
                    }
                });
    }

    private void observeBudgetActions() {

        budgetViewModel.getBudgetAction().observe(
                getViewLifecycleOwner(),
                resource -> {

                    if (resource == null) {
                        return;
                    }

                    if (resource.getStatus()
                            == Resource.Status.ERROR) {

                        Toast.makeText(
                                requireContext(),
                                resource.getMessage(),
                                Toast.LENGTH_LONG).show();

                        budgetViewModel.resetBudgetAction();
                    }
                });

        budgetViewModel.getDeleteAction().observe(
                getViewLifecycleOwner(),
                resource -> {

                    if (resource == null) {
                        return;
                    }

                    switch (resource.getStatus()) {

                        case SUCCESS:
                            Toast.makeText(
                                    requireContext(),
                                    "Đã xóa ngân sách.",
                                    Toast.LENGTH_SHORT).show();

                            budgetViewModel.resetDeleteAction();
                            budgetViewModel.loadBudgets();
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    resource.getMessage(),
                                    Toast.LENGTH_LONG).show();

                            budgetViewModel.resetDeleteAction();
                            break;

                        case LOADING:
                        case IDLE:
                        default:
                            break;
                    }
                });
    }

    private void showCreateBudget() {

        budgetViewModel.resetBudgetAction();

        AddEditBudgetBottomSheet sheet =
                AddEditBudgetBottomSheet.newCreateInstance();

        sheet.show(
                getParentFragmentManager(),
                "AddEditBudgetBottomSheet");
    }

    private void showEditBudget(Budget budget) {

        budgetViewModel.resetBudgetAction();

        AddEditBudgetBottomSheet sheet =
                AddEditBudgetBottomSheet.newEditInstance(budget);

        sheet.show(
                getParentFragmentManager(),
                "AddEditBudgetBottomSheet");
    }

    private void showDeleteBudget(Budget budget) {

        ConfirmDeleteDialog.show(
                requireContext(),
                "Xóa ngân sách",
                "Bạn có chắc chắn muốn xóa ngân sách \""
                        + budget.getCategoryName()
                        + "\" tháng "
                        + budget.getMonth()
                        + "/"
                        + budget.getYear()
                        + "?",
                () -> budgetViewModel.deleteBudget(
                        budget.getId()));
    }
}
