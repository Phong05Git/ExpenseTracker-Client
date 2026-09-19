package com.example.expensetracker.presentation.ui.fragment.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.presentation.adapter.TransactionAdapter;
import com.example.expensetracker.presentation.ui.bottomsheet.AddEditTransactionBottomSheet;
import com.example.expensetracker.presentation.ui.bottomsheet.FilterTransactionsBottomSheet;
import com.example.expensetracker.presentation.ui.calendar.TransactionCalendarView;
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel;
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class TransactionsFragment extends Fragment {

    private TransactionViewModel viewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionAdapter transactionAdapter;
    private TransactionCalendarView calendarView;

    private TextView tvCurrentMonth;
    private ImageButton btnPreviousMonth;
    private ImageButton btnNextMonth;
    private ImageButton btnTransactionFilter;

    private Calendar selectedDate;
    private Calendar displayedMonth;

    private String activeFromDate;
    private String activeToDate;
    private Integer activeCategoryId;
    private Integer activeType;
    private String activeKeyword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        getParentFragmentManager().setFragmentResultListener(
                AddEditTransactionBottomSheet.TRANSACTION_CHANGED_RESULT,
                getViewLifecycleOwner(),
                (requestKey, result) -> refreshAfterTransactionChanged());

        calendarView = view.findViewById(R.id.calendarView);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        btnTransactionFilter = view.findViewById(R.id.btnTransactionFilter);

        RecyclerView recyclerTransactions = view.findViewById(R.id.recyclerTransactions);

        selectedDate = Calendar.getInstance();
        displayedMonth = Calendar.getInstance();
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);

        transactionAdapter = new TransactionAdapter(
                requireContext(),
                this::showEditTransactionBottomSheet);

        recyclerTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTransactions.setAdapter(transactionAdapter);

        calendarView.setSelectedDate(selectedDate);
        calendarView.setMonth(displayedMonth);

        observeViewModel();
        observeCategories();
        setupListeners();

        updateMonthTitle();

        categoryViewModel.loadCategories();
        loadCurrentMonth();
        loadSelectedDate();
    }

    private void observeViewModel() {
        viewModel.getMonthTransactions().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || resource.getData() == null) {
                return;
            }

            calendarView.setTransactions(resource.getData());
        });

        viewModel.getTransactions().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || resource.getData() == null) {
                return;
            }

            if (!hasActiveFilter()) {
                transactionAdapter.setItems(resource.getData());
            }
        });

        viewModel.getFilteredTransactions().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || resource.getData() == null) {
                return;
            }

            if (hasActiveFilter()) {
                transactionAdapter.setItems(resource.getData());
            }
        });
    }

    private void observeCategories() {
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || resource.getData() == null) {
                return;
            }

            transactionAdapter.setCategories(resource.getData());
        });
    }

    private void setupListeners() {
        calendarView.setOnDateSelectedListener(date -> {
            selectedDate = (Calendar) date.clone();

            clearActiveFilter();
            loadSelectedDate();
        });

        btnPreviousMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));
        btnTransactionFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showEditTransactionBottomSheet(Transaction transaction) {
        viewModel.resetTransactionAction();
        viewModel.resetDeleteAction();

        AddEditTransactionBottomSheet bottomSheet =
                AddEditTransactionBottomSheet.newEditInstance(viewModel, transaction);

        bottomSheet.show(
                getParentFragmentManager(),
                "AddEditTransactionBottomSheet");
    }

    private void refreshAfterTransactionChanged() {
        loadCurrentMonth();

        if (hasActiveFilter()) {
            applyActiveFilter();
        } else {
            loadSelectedDate();
        }
    }

    private void showFilterBottomSheet() {
        FilterTransactionsBottomSheet bottomSheet = new FilterTransactionsBottomSheet();

        bottomSheet.setOnFilterAppliedListener(
                (fromDate, toDate, categoryId, type, keyword) -> {
                    activeFromDate = fromDate;
                    activeToDate = toDate;
                    activeCategoryId = categoryId;
                    activeType = type;
                    activeKeyword = keyword;

                    if (!hasActiveFilter()) {
                        loadSelectedDate();
                        return;
                    }

                    applyActiveFilter();
                });

        bottomSheet.show(
                getParentFragmentManager(),
                "FilterTransactionsBottomSheet");
    }

    private void applyActiveFilter() {
        viewModel.filterTransactions(
                activeFromDate,
                activeToDate,
                activeCategoryId,
                activeType,
                activeKeyword);
    }

    private void clearActiveFilter() {
        activeFromDate = null;
        activeToDate = null;
        activeCategoryId = null;
        activeType = null;
        activeKeyword = null;
    }

    private boolean hasActiveFilter() {
        return activeFromDate != null
                || activeToDate != null
                || activeCategoryId != null
                || activeType != null
                || activeKeyword != null;
    }

    private void loadCurrentMonth() {
        Calendar firstDay = (Calendar) displayedMonth.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);

        Calendar firstDayNextMonth = (Calendar) firstDay.clone();
        firstDayNextMonth.add(Calendar.MONTH, 1);

        viewModel.loadMonth(
                formatApiDate(firstDay),
                formatApiDate(firstDayNextMonth));
    }

    private void loadSelectedDate() {
        viewModel.loadByDate(formatApiDate(selectedDate));
    }

    private void changeMonth(int amount) {
        displayedMonth.add(Calendar.MONTH, amount);

        calendarView.setMonth(displayedMonth);
        updateMonthTitle();
        loadCurrentMonth();

        if (hasActiveFilter()) {
            applyActiveFilter();
        } else if (isSameMonth(selectedDate, displayedMonth)) {
            loadSelectedDate();
        } else {
            transactionAdapter.setItems(null);
        }
    }

    private boolean isSameMonth(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.MONTH) == second.get(Calendar.MONTH);
    }

    private void updateMonthTitle() {
        SimpleDateFormat format = new SimpleDateFormat(
                "MMMM yyyy",
                new Locale("vi", "VN"));

        String title = format.format(displayedMonth.getTime());

        title = title.substring(0, 1).toUpperCase(Locale.getDefault())
                + title.substring(1);

        tvCurrentMonth.setText(title);
    }

    private String formatApiDate(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US);

        return format.format(calendar.getTime());
    }
}