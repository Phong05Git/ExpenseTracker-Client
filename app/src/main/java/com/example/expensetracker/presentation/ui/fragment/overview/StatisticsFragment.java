package com.example.expensetracker.presentation.ui.fragment.overview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.model.CategoryBreakdown;
import com.example.expensetracker.domain.model.StatisticsData;
import com.example.expensetracker.presentation.adapter.CategoryBreakdownAdapter;
import com.example.expensetracker.presentation.viewmodel.StatisticsViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticsFragment extends Fragment {

    private static final String ARG_PERIOD = "period";
    private static final String ARG_REFERENCE_DATE = "referenceDate";
    private static final String ARG_START_DATE = "startDate";
    private static final String ARG_END_DATE = "endDate";

    private StatisticsViewModel viewModel;

    private LinearLayout categoryContainer;
    private LinearLayout incomeCategoryContainer;

    private PieChart pieChart;
    private PieChart incomePieChart;

    private int period = 2;
    private String referenceDate;
    private String startDate;
    private String endDate;

    public static StatisticsFragment newInstance(int period, String referenceDate, String startDate, String endDate) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PERIOD, period);
        args.putString(ARG_REFERENCE_DATE, referenceDate);
        args.putString(ARG_START_DATE, startDate);
        args.putString(ARG_END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            period = getArguments().getInt(ARG_PERIOD, 2);
            referenceDate = getArguments().getString(ARG_REFERENCE_DATE);
            startDate = getArguments().getString(ARG_START_DATE);
            endDate = getArguments().getString(ARG_END_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        pieChart = view.findViewById(R.id.pieChart);
        incomePieChart = view.findViewById(R.id.incomePieChart);
        categoryContainer = view.findViewById(R.id.categoryContainer);
        incomeCategoryContainer = view.findViewById(R.id.incomeCategoryContainer);

        setupPieChart(pieChart, "Chi tiêu");
        setupPieChart(incomePieChart, "Thu nhập");

        viewModel.getStatistics().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getData() != null) {
                displayStatistics(resource.getData());
            }
        });

        loadStatistics();
    }

    public void setPeriod(int period, String referenceDate, String startDate, String endDate) {
        this.period = period;
        this.referenceDate = referenceDate;
        this.startDate = startDate;
        this.endDate = endDate;

        if (viewModel != null && isAdded()) {
            loadStatistics();
        }
    }

    private void loadStatistics() {
        viewModel.loadStatistics(period, referenceDate, startDate, endDate);
    }

    private void displayStatistics(StatisticsData data) {
        setPieChartData(pieChart, data.getExpenseCategoryBreakdown());
        setPieChartData(incomePieChart, data.getIncomeCategoryBreakdown());

        categoryContainer.removeAllViews();

        CategoryBreakdownAdapter expenseAdapter = new CategoryBreakdownAdapter(requireContext());
        expenseAdapter.setItems(data.getExpenseCategoryBreakdown());
        expenseAdapter.bind(categoryContainer);

        incomeCategoryContainer.removeAllViews();

        CategoryBreakdownAdapter incomeAdapter = new CategoryBreakdownAdapter(requireContext());
        incomeAdapter.setItems(data.getIncomeCategoryBreakdown());
        incomeAdapter.bind(incomeCategoryContainer);
    }

    private void setupPieChart(PieChart chart, String centerText) {
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setCenterText(centerText);
        chart.setCenterTextSize(16f);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(60f);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    private void setPieChartData(PieChart chart, List<CategoryBreakdown> categories) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (CategoryBreakdown item : categories) {
            if (item.getAmount() <= 0) {
                continue;
            }

            entries.add(new PieEntry((float) item.getAmount(), item.getCategoryName()));
            colors.add(parseCategoryColor(item.getCategoryColor()));
        }

        if (entries.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, chart == pieChart ? "Chi tiêu" : "Thu nhập");
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
    }

    private int parseCategoryColor(String categoryColor) {
        if (categoryColor != null && !categoryColor.isBlank()) {
            try {
                return Color.parseColor(categoryColor);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return requireContext().getColor(R.color.primary_green);
    }
}