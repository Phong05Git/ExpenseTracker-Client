package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.presentation.ui.activity.LoginActivity;
import com.example.expensetracker.presentation.ui.activity.ProfileActivity;
import com.example.expensetracker.presentation.ui.bottomsheet.AddEditTransactionBottomSheet;
import com.example.expensetracker.presentation.ui.fragment.budget.BudgetsFragment;
import com.example.expensetracker.presentation.ui.fragment.category.CategoriesFragment;
import com.example.expensetracker.presentation.ui.fragment.overview.OverviewFragment;
import com.example.expensetracker.presentation.ui.fragment.transaction.TransactionsFragment;
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel;
import com.example.expensetracker.security.IdleTimeoutManager;
import com.example.expensetracker.security.ScreenSecurityHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private LinearLayout navOverview;
    private LinearLayout navTransactions;
    private LinearLayout navCategories;
    private LinearLayout navBudgets;

    private ImageView iconOverview;
    private ImageView iconTransactions;
    private ImageView iconCategories;
    private ImageView iconBudgets;
    private ImageView iconAccount;

    private TextView labelOverview;
    private TextView labelTransactions;
    private TextView labelCategories;
    private TextView labelBudgets;

    private FloatingActionButton fabAdd;

    private int selectedGreen;
    private int inactiveColor;

    private TransactionViewModel transactionViewModel;
    private String selectedNavigationTag;

    @Inject
    SessionManager sessionManager;

    @Inject
    IdleTimeoutManager idleTimeoutManager;

    @Inject
    ScreenSecurityHelper screenSecurityHelper;

    private final CompositeDisposable sessionDisposables =
            new CompositeDisposable();

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenSecurityHelper.enable(this);

        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        selectedGreen =
                ContextCompat.getColor(
                        this,
                        R.color.primary_green);

        inactiveColor =
                ContextCompat.getColor(
                        this,
                        R.color.light_text_secondary);

        initializeViews();
        observeSessionExpired();
        setupListeners();

        transactionViewModel =
                new ViewModelProvider(this)
                        .get(TransactionViewModel.class);

        if (savedInstanceState == null) {
            showFragment(
                    new OverviewFragment(),
                    "overview");
        } else {
            updateSelectedNavigation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager.isSessionActive()) {
            idleTimeoutManager.start();
        }
    }

    @Override
    protected void onPause() {
        idleTimeoutManager.stop();
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            idleTimeoutManager.reset();
        }

        return super.dispatchTouchEvent(event);
    }

    private void initializeViews() {
        navOverview = findViewById(R.id.navOverview);

        navTransactions = findViewById(R.id.navTransactions);

        navCategories = findViewById(R.id.navCategories);

        navBudgets = findViewById(R.id.navBudgets);

        iconOverview = findViewById(R.id.iconOverview);

        iconTransactions = findViewById(R.id.iconTransactions);

        iconCategories = findViewById(R.id.iconCategories);

        iconBudgets = findViewById(R.id.iconBudgets);

        iconAccount = findViewById(R.id.iconAccount);

        labelOverview = findViewById(R.id.labelOverview);

        labelTransactions = findViewById(R.id.labelTransactions);

        labelCategories = findViewById(R.id.labelCategories);

        labelBudgets = findViewById(R.id.labelBudgets);

        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupListeners() {
        navOverview.setOnClickListener(
                v -> showFragment(
                        new OverviewFragment(),
                        "overview"));

        navTransactions.setOnClickListener(
                v -> showFragment(
                        new TransactionsFragment(),
                        "transactions"));

        navCategories.setOnClickListener(
                v -> showFragment(
                        new CategoriesFragment(),
                        "categories"));

        navBudgets.setOnClickListener(
                v -> showFragment(
                        new BudgetsFragment(),
                        "budgets"));

        iconAccount.setOnClickListener(
                v -> openProfile());

        fabAdd.setOnClickListener(
                v -> openAddTransaction());
    }

    private void observeSessionExpired() {
        sessionDisposables.add(
                sessionManager
                        .observeSessionExpired()
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                value -> openLoginActivity()));
    }

    private void openLoginActivity() {
        idleTimeoutManager.stop();

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void showFragment(
            Fragment fragment,
            String tag) {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.isStateSaved()) {
            return;
        }

        String currentTag = selectedNavigationTag;

        if (currentTag == null) {
            currentTag = getVisibleNavigationTag(fragmentManager);
        }

        if (tag.equals(currentTag)) {
            updateNavigation(tag);
            return;
        }

        Fragment currentFragment = currentTag == null
                ? null
                : fragmentManager.findFragmentByTag(currentTag);

        Fragment targetFragment = fragmentManager.findFragmentByTag(tag);

        if (targetFragment == null) {
            targetFragment = fragment;
        }

        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true);

        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        } else {
            transaction.add(
                    R.id.nav_host_fragment,
                    targetFragment,
                    tag);
        }

        transaction.commitNow();

        selectedNavigationTag = tag;

        updateNavigation(tag);
    }

    private String getVisibleNavigationTag(
            FragmentManager fragmentManager) {
        if (isFragmentVisible(fragmentManager, "overview")) {
            return "overview";
        }

        if (isFragmentVisible(fragmentManager, "transactions")) {
            return "transactions";
        }

        if (isFragmentVisible(fragmentManager, "categories")) {
            return "categories";
        }

        if (isFragmentVisible(fragmentManager, "budgets")) {
            return "budgets";
        }

        return null;
    }

    private boolean isFragmentVisible(
            FragmentManager fragmentManager,
            String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        return fragment != null && fragment.isVisible();
    }

    private void updateNavigation(
            String tag) {
        boolean overviewSelected =
                "overview".equals(tag);

        boolean transactionsSelected =
                "transactions".equals(tag);

        boolean categoriesSelected =
                "categories".equals(tag);

        boolean budgetsSelected =
                "budgets".equals(tag);

        updateNavigationItem(
                iconOverview,
                labelOverview,
                overviewSelected);

        updateNavigationItem(
                iconTransactions,
                labelTransactions,
                transactionsSelected);

        updateNavigationItem(
                iconCategories,
                labelCategories,
                categoriesSelected);

        updateNavigationItem(
                iconBudgets,
                labelBudgets,
                budgetsSelected);
    }

    private void updateNavigationItem(
            ImageView icon,
            TextView label,
            boolean selected) {
        int color =
                selected
                        ? selectedGreen
                        : inactiveColor;

        icon.setColorFilter(color);
        label.setTextColor(color);
        label.setTypeface(
                selected
                        ? Typeface.DEFAULT_BOLD
                        : Typeface.DEFAULT);
    }

    private void updateSelectedNavigation() {
        selectedNavigationTag = getVisibleNavigationTag(
                getSupportFragmentManager());

        if (selectedNavigationTag == null) {
            updateNavigation("overview");
            return;
        }

        updateNavigation(selectedNavigationTag);
    }

    private void openProfile() {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        startActivity(
                new Intent(
                        this,
                        ProfileActivity.class));
    }

    private void openAddTransaction() {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        transactionViewModel.resetTransactionAction();

        AddEditTransactionBottomSheet sheet =
                AddEditTransactionBottomSheet
                        .newCreateInstance(
                                transactionViewModel);

        sheet.show(
                getSupportFragmentManager(),
                "AddEditTransactionBottomSheet");
    }

    @Override
    protected void onDestroy() {
        sessionDisposables.clear();
        super.onDestroy();
    }
}
