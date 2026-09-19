package com.example.expensetracker.presentation.ui.fragment.overview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.presentation.ui.bottomsheet.DateRangePickerBottomSheet;
import com.example.expensetracker.presentation.viewmodel.OverviewViewModel;
import com.example.expensetracker.util.DateUtils;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OverviewFragment extends Fragment {

    private static final int OPTION_THIS_MONTH = 1;
    private static final int OPTION_THIS_WEEK = 2;
    private static final int OPTION_THIS_YEAR = 3;
    private static final int OPTION_CUSTOM = 4;

    private OverviewViewModel viewModel;
    private View overviewContent;
    private FrameLayout statisticsContainer;
    private TextView tabOverview;
    private TextView tabStatistics;
    private TextView tvIncomeQuick;
    private TextView tvExpenseQuick;
    private TextView tvBalance;
    private MaterialCardView periodSelector;
    private TextView tvSelectedPeriod;

    private int selectedOption = OPTION_THIS_MONTH;
    private Calendar customStartDate;
    private Calendar customEndDate;
    private boolean statisticsSelected;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OverviewViewModel.class);

        overviewContent = view.findViewById(R.id.overviewContent);
        statisticsContainer = view.findViewById(R.id.statisticsContainer);
        tabOverview = view.findViewById(R.id.tabOverview);
        tabStatistics = view.findViewById(R.id.tabStatistics);
        tvIncomeQuick = view.findViewById(R.id.tvIncomeQuick);
        tvExpenseQuick = view.findViewById(R.id.tvExpenseQuick);
        tvBalance = view.findViewById(R.id.tvBalance);
        periodSelector = view.findViewById(R.id.periodSelector);
        tvSelectedPeriod = view.findViewById(R.id.tvSelectedPeriod);

        viewModel.getStatistics().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getData() != null && !statisticsSelected) {
                displayOverview(resource.getData());
            }
        });

        tabOverview.setOnClickListener(v -> showOverview());
        tabStatistics.setOnClickListener(v -> showStatistics());
        periodSelector.setOnClickListener(v -> showPeriodDropdown());

        updatePeriodSelectorText();
        showOverview();
    }

    private void showPeriodDropdown() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), periodSelector);

        popupMenu.getMenu().add(0, OPTION_THIS_MONTH, 0, "Tháng này");
        popupMenu.getMenu().add(0, OPTION_THIS_WEEK, 1, "Tuần này");
        popupMenu.getMenu().add(0, OPTION_THIS_YEAR, 2, "Năm này");
        popupMenu.getMenu().add(0, OPTION_CUSTOM, 3, "Tùy chỉnh thời gian");

        popupMenu.setOnMenuItemClickListener(item -> {
            selectPeriod(item.getItemId());
            return true;
        });

        popupMenu.show();
    }

    private void selectPeriod(int option) {
        if (option == OPTION_CUSTOM) {
            showCustomDateRangePicker();
            return;
        }

        selectedOption = option;
        updatePeriodSelectorText();

        if (statisticsSelected) {
            showStatistics();
        } else {
            showOverview();
        }
    }

    private void showCustomDateRangePicker() {
        DateRangePickerBottomSheet sheet = DateRangePickerBottomSheet.newInstance(customStartDate, customEndDate);

        sheet.setOnDateRangeConfirmedListener((startDate, endDate) -> {
            customStartDate = (Calendar) startDate.clone();
            customEndDate = (Calendar) endDate.clone();
            selectedOption = OPTION_CUSTOM;
            updatePeriodSelectorText();

            if (statisticsSelected) {
                showStatistics();
            } else {
                showOverview();
            }
        });

        sheet.show(getParentFragmentManager(), "DateRangePickerBottomSheet");
    }

    private void showOverview() {
        statisticsSelected = false;
        overviewContent.setVisibility(View.VISIBLE);
        statisticsContainer.setVisibility(View.GONE);
        updateTabs(false);
        loadCurrentSelectionForOverview();
    }

    private void showStatistics() {
        statisticsSelected = true;
        overviewContent.setVisibility(View.GONE);
        statisticsContainer.setVisibility(View.VISIBLE);
        updateTabs(true);

        int apiPeriod = getApiPeriod();
        String referenceDate = getReferenceDate();
        String startDate = getCustomStartDate();
        String endDate = getCustomEndDate();

        Fragment fragment = getChildFragmentManager().findFragmentByTag("statistics");

        if (fragment == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(
                            R.id.statisticsContainer,
                            StatisticsFragment.newInstance(apiPeriod, referenceDate, startDate, endDate),
                            "statistics")
                    .commit();
            return;
        }

        if (fragment instanceof StatisticsFragment) {
            ((StatisticsFragment) fragment).setPeriod(apiPeriod, referenceDate, startDate, endDate);
        }
    }

    private void loadCurrentSelectionForOverview() {
        viewModel.loadStatistics(
                getApiPeriod(),
                getReferenceDate(),
                getCustomStartDate(),
                getCustomEndDate());
    }

    private int getApiPeriod() {
        return switch (selectedOption) {
            case OPTION_THIS_WEEK -> 1;
            case OPTION_THIS_YEAR -> 3;
            default -> 2;
        };
    }

    private String getReferenceDate() {
        if (selectedOption == OPTION_CUSTOM) {
            return null;
        }

        return DateUtils.formatApiDate(Calendar.getInstance());
    }

    private String getCustomStartDate() {
        if (selectedOption != OPTION_CUSTOM || customStartDate == null) {
            return null;
        }

        return DateUtils.formatApiDate(customStartDate);
    }

    private String getCustomEndDate() {
        if (selectedOption != OPTION_CUSTOM || customEndDate == null) {
            return null;
        }

        return DateUtils.formatApiDate(customEndDate);
    }

    @SuppressLint("SetTextI18n")
    private void updatePeriodSelectorText() {
        switch (selectedOption) {
            case OPTION_THIS_WEEK -> tvSelectedPeriod.setText("Tuần này");
            case OPTION_THIS_YEAR -> tvSelectedPeriod.setText("Năm này");
            case OPTION_CUSTOM -> {
                if (customStartDate != null && customEndDate != null) {
                    tvSelectedPeriod.setText(
                            DateUtils.formatDisplayDate(customStartDate) +
                                    " - " +
                                    DateUtils.formatDisplayDate(customEndDate));
                } else {
                    tvSelectedPeriod.setText("Tùy chỉnh thời gian");
                }
            }
            default -> tvSelectedPeriod.setText("Tháng này");
        }

        tvSelectedPeriod.setTextColor(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.light_text_primary));
    }

    private void updateTabs(boolean statisticsSelected) {
        int selected = ContextCompat.getColor(requireContext(), R.color.primary_green);
        int inactive = ContextCompat.getColor(requireContext(), R.color.light_text_secondary);

        tabOverview.setTextColor(statisticsSelected ? inactive : selected);
        tabStatistics.setTextColor(statisticsSelected ? selected : inactive);
    }

    private void displayOverview(StatisticsData data) {
        double balance = data.getTotalIncome() - data.getTotalExpense();

        tvIncomeQuick.setText(formatMoney(data.getTotalIncome()));
        tvExpenseQuick.setText(formatMoney(data.getTotalExpense()));
        tvBalance.setText(formatMoney(balance));

        tvIncomeQuick.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.income_blue));

        tvExpenseQuick.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.expense_red));
    }

    private String formatMoney(double amount) {
        NumberFormat formatter =
                NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount) + " ₫";
    }
}