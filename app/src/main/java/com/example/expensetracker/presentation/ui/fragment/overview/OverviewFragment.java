package com.example.expensetracker.presentation.ui.fragment.overview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.CategoryBreakdown;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.presentation.adapter.CategoryBreakdownAdapter;
import com.example.expensetracker.presentation.ui.bottomsheet.DateRangePickerBottomSheet;
import com.example.expensetracker.presentation.viewmodel.OverviewViewModel;
import com.example.expensetracker.util.DateUtils;
import com.example.expensetracker.util.RxBus;
import com.example.expensetracker.util.TransactionUpdatedEvent;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class OverviewFragment extends Fragment {

    private static final int OPTION_THIS_MONTH = 1;
    private static final int OPTION_THIS_WEEK = 2;
    private static final int OPTION_THIS_YEAR = 3;
    private static final int OPTION_CUSTOM = 4;

    private OverviewViewModel viewModel;

    private TextView tvSelectedPeriod;
    private TextView tvIncome;
    private TextView tvExpense;
    private TextView tvNet;
    private TextView tvCategoryEmpty;
    private TextView tvIncomeCategoryEmpty;
    private TextView tvStatisticsError;
    private View statisticsContent;
    private ProgressBar progressStatistics;
    private PieChart incomeCategoryChart;
    private PieChart expenseCategoryChart;
    private android.widget.LinearLayout incomeCategoryContainer;
    private android.widget.LinearLayout categoryContainer;

    private int selectedOption = OPTION_THIS_MONTH;
    private Calendar customStartDate;
    private Calendar customEndDate;

    private final CompositeDisposable disposables = new CompositeDisposable();

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

        tvSelectedPeriod = view.findViewById(R.id.tvSelectedPeriod);
        tvIncome = view.findViewById(R.id.tvIncomeQuick);
        tvExpense = view.findViewById(R.id.tvExpenseQuick);
        tvNet = view.findViewById(R.id.tvBalance);
        tvCategoryEmpty = view.findViewById(R.id.tvCategoryEmpty);
        tvIncomeCategoryEmpty = view.findViewById(R.id.tvIncomeCategoryEmpty);
        tvStatisticsError = view.findViewById(R.id.tvStatisticsError);
        statisticsContent = view.findViewById(R.id.statisticsContent);
        progressStatistics = view.findViewById(R.id.progressStatistics);
        incomeCategoryChart = view.findViewById(R.id.incomeCategoryChart);
        expenseCategoryChart = view.findViewById(R.id.categoryChart);
        incomeCategoryContainer = view.findViewById(R.id.incomeCategoryContainer);
        categoryContainer = view.findViewById(R.id.categoryContainer);

        setupPieChart(incomeCategoryChart, "Thu nhập", "Chưa có dữ liệu danh mục thu nhập.");
        setupPieChart(expenseCategoryChart, "Chi tiêu", "Chưa có dữ liệu danh mục chi tiêu.");
        updatePeriodSelectorText();

        viewModel.getStatistics().observe(getViewLifecycleOwner(), this::renderStatisticsState);

        disposables.add(
                RxBus.getInstance()
                        .toObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            if (event instanceof TransactionUpdatedEvent) {
                                loadStatistics(true);
                            }
                        }, throwable -> {
                            // RxBus observer errors must not crash the Dashboard.
                        })
        );

        view.findViewById(R.id.periodSelector).setOnClickListener(v -> showPeriodDropdown());
        loadStatistics(false);
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }

    private void showPeriodDropdown() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), requireView().findViewById(R.id.periodSelector));
        popupMenu.getMenu().add(0, OPTION_THIS_WEEK, 0, "Tuần này");
        popupMenu.getMenu().add(0, OPTION_THIS_MONTH, 1, "Tháng này");
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

        if (selectedOption == option) {
            return;
        }

        selectedOption = option;
        updatePeriodSelectorText();
        loadStatistics(false);
    }

    private void showCustomDateRangePicker() {
        DateRangePickerBottomSheet sheet =
                DateRangePickerBottomSheet.newInstance(customStartDate, customEndDate);

        sheet.setOnDateRangeConfirmedListener((startDate, endDate) -> {
            customStartDate = (Calendar) startDate.clone();
            customEndDate = (Calendar) endDate.clone();
            selectedOption = OPTION_CUSTOM;
            updatePeriodSelectorText();
            loadStatistics(false);
        });

        sheet.show(getParentFragmentManager(), "DateRangePickerBottomSheet");
    }

    private void loadStatistics(boolean forceRefresh) {
        int period = getApiPeriod();
        String referenceDate = getReferenceDate();
        String startDate = getCustomStartDate();
        String endDate = getCustomEndDate();

        if (forceRefresh) {
            viewModel.refreshStatistics(period, referenceDate, startDate, endDate);
        } else {
            viewModel.loadStatistics(period, referenceDate, startDate, endDate);
        }
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
                            DateUtils.formatDisplayDate(customStartDate)
                                    + " - " + DateUtils.formatDisplayDate(customEndDate));
                } else {
                    tvSelectedPeriod.setText("Tùy chỉnh thời gian");
                }
            }
            default -> tvSelectedPeriod.setText("Tháng này");
        }
    }

    private void renderStatisticsState(Resource<StatisticsData> resource) {
        if (resource == null) {
            return;
        }

        boolean loading = resource.getStatus() == Resource.Status.LOADING;
        progressStatistics.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            tvStatisticsError.setVisibility(View.GONE);
        }

        if (loading && resource.getData() == null) {
            statisticsContent.setVisibility(View.GONE);
        }

        if (resource.getStatus() == Resource.Status.ERROR) {
            tvStatisticsError.setText(resource.getMessage() == null
                    ? "Không thể tải dữ liệu thống kê." : resource.getMessage());
            tvStatisticsError.setVisibility(View.VISIBLE);
            return;
        }

        if (resource.getData() != null) {
            tvStatisticsError.setVisibility(View.GONE);
            statisticsContent.setVisibility(View.VISIBLE);
            displayStatistics(resource.getData());
        } else if (resource.getStatus() == Resource.Status.IDLE) {
            statisticsContent.setVisibility(View.GONE);
        }
    }

    private void displayStatistics(StatisticsData data) {
        double net = data.getTotalIncome() - data.getTotalExpense();
        tvIncome.setText(formatMoney(data.getTotalIncome()));
        tvExpense.setText(formatMoney(data.getTotalExpense()));
        tvNet.setText(formatMoney(net));

        displayCategoryChart(
                incomeCategoryChart,
                incomeCategoryContainer,
                tvIncomeCategoryEmpty,
                data.getIncomeCategoryBreakdown(),
                "Chưa có dữ liệu danh mục thu nhập.");
        displayCategoryChart(
                expenseCategoryChart,
                categoryContainer,
                tvCategoryEmpty,
                data.getExpenseCategoryBreakdown(),
                "Chưa có dữ liệu danh mục chi tiêu.");
    }

    private void setupPieChart(PieChart chart, String centerText, String noDataText) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setCenterText(centerText);
        chart.setCenterTextSize(16f);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(62f);
        chart.setNoDataText(noDataText);
        chart.getLegend().setEnabled(false);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(false);
    }

    private void displayCategoryChart(
            PieChart chart,
            android.widget.LinearLayout container,
            TextView emptyView,
            List<CategoryBreakdown> categories,
            String noDataText) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (categories != null) {
            for (CategoryBreakdown item : categories) {
                if (item.getAmount() <= 0) {
                    continue;
                }
                entries.add(new PieEntry((float) item.getAmount(), item.getCategoryName()));
                colors.add(parseCategoryColor(item.getCategoryColor()));
            }
        }

        container.removeAllViews();

        if (entries.isEmpty()) {
            chart.clear();
            chart.setNoDataText(noDataText);
            chart.invalidate();
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);

        PieDataSet dataSet = new PieDataSet(entries, "Danh mục");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.1f%%", value);
            }
        });

        chart.setData(pieData);
        chart.invalidate();

        CategoryBreakdownAdapter adapter = new CategoryBreakdownAdapter(requireContext());
        adapter.setItems(categories);
        adapter.bind(container);
    }

    private int parseCategoryColor(String value) {
        if (value != null && !value.isBlank()) {
            try {
                return Color.parseColor(value);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ContextCompat.getColor(requireContext(), R.color.primary_green);
    }

    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " ₫";
    }
}
