
package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.expensetracker.presentation.ui.fragment.chatbot.ChatbotFragment;
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

    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_TRANSACTIONS = "transactions";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_BUDGETS = "budgets";
    private static final String TAG_CHATBOT = "chatbot";

    private static final int MENU_TRANSACTIONS = 1;
    private static final int MENU_CATEGORIES = 2;
    private static final int MENU_BUDGETS = 3;

    private LinearLayout navOverview;
    private LinearLayout navFinance;
    private LinearLayout navGroups;
    private LinearLayout navChatbot;

    private ImageView iconOverview;
    private ImageView iconFinance;
    private ImageView iconGroups;
    private ImageView iconChatbot;
    private ImageView iconAccount;
    private ImageView iconNotification;

    private TextView labelOverview;
    private TextView labelFinance;
    private TextView labelGroups;
    private TextView labelChatbot;

    private FloatingActionButton fabAdd;

    private int selectedColor;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenSecurityHelper.enable(this);

        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        selectedColor = ContextCompat.getColor(
                this,
                R.color.primary_green
        );

        inactiveColor = ContextCompat.getColor(
                this,
                R.color.light_text_secondary
        );

        initializeViews();
        setupListeners();
        observeSessionExpired();

        transactionViewModel = new ViewModelProvider(this)
                .get(TransactionViewModel.class);

        if (savedInstanceState == null) {
            showFragment(
                    new OverviewFragment(),
                    TAG_OVERVIEW
            );
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
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_MOVE) {
            idleTimeoutManager.reset();
        }

        return super.dispatchTouchEvent(event);
    }

    private void initializeViews() {
        navOverview = findViewById(R.id.navOverview);
        navFinance = findViewById(R.id.navFinance);
        navGroups = findViewById(R.id.navGroups);
        navChatbot = findViewById(R.id.navChatbot);

        iconOverview = findViewById(R.id.iconOverview);
        iconFinance = findViewById(R.id.iconFinance);
        iconGroups = findViewById(R.id.iconGroups);
        iconChatbot = findViewById(R.id.iconChatbot);
        iconAccount = findViewById(R.id.iconAccount);
        iconNotification = findViewById(R.id.iconNotification);

        labelOverview = findViewById(R.id.labelOverview);
        labelFinance = findViewById(R.id.labelFinance);
        labelGroups = findViewById(R.id.labelGroups);
        labelChatbot = findViewById(R.id.labelChatbot);

        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupListeners() {
        navOverview.setOnClickListener(v ->
                showFragment(
                        new OverviewFragment(),
                        TAG_OVERVIEW
                )
        );

        navFinance.setOnClickListener(v ->
                showFinanceMenu()
        );

        navGroups.setOnClickListener(v ->
                showNotImplementedMessage("Quản lý nhóm")
        );

        navChatbot.setOnClickListener(v ->
                showFragment(
                        new ChatbotFragment(),
                        TAG_CHATBOT
                )
        );

        iconNotification.setOnClickListener(v ->
                showNotImplementedMessage("Thông báo")
        );

        iconAccount.setOnClickListener(v ->
                openProfile()
        );

        fabAdd.setOnClickListener(v ->
                openAddTransaction()
        );
    }

    private void showFinanceMenu() {
        PopupMenu popupMenu = new PopupMenu(
                this,
                navFinance
        );

        popupMenu.getMenu().add(
                0,
                MENU_TRANSACTIONS,
                0,
                "Giao dịch"
        );

        popupMenu.getMenu().add(
                0,
                MENU_CATEGORIES,
                1,
                "Danh mục"
        );

        popupMenu.getMenu().add(
                0,
                MENU_BUDGETS,
                2,
                "Ngân sách"
        );

        popupMenu.setOnMenuItemClickListener(
                this::onFinanceMenuItemSelected
        );

        popupMenu.show();
    }

    private boolean onFinanceMenuItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == MENU_TRANSACTIONS) {
            showFragment(
                    new TransactionsFragment(),
                    TAG_TRANSACTIONS
            );
            return true;
        }

        if (itemId == MENU_CATEGORIES) {
            showFragment(
                    new CategoriesFragment(),
                    TAG_CATEGORIES
            );
            return true;
        }

        if (itemId == MENU_BUDGETS) {
            showFragment(
                    new BudgetsFragment(),
                    TAG_BUDGETS
            );
            return true;
        }

        return false;
    }

    private void observeSessionExpired() {
        sessionDisposables.add(
                sessionManager
                        .observeSessionExpired()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                value -> openLoginActivity()
                        )
        );
    }

    private void openLoginActivity() {
        idleTimeoutManager.stop();

        Intent intent = new Intent(
                this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void showFragment(Fragment fragment, String tag) {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        FragmentManager fragmentManager =
                getSupportFragmentManager();

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

        Fragment targetFragment =
                fragmentManager.findFragmentByTag(tag);

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
                    tag
            );
        }

        transaction.commitNow();

        selectedNavigationTag = tag;
        updateNavigation(tag);
    }

    private String getVisibleNavigationTag(
            FragmentManager fragmentManager
    ) {
        if (isFragmentVisible(fragmentManager, TAG_OVERVIEW)) {
            return TAG_OVERVIEW;
        }

        if (isFragmentVisible(fragmentManager, TAG_TRANSACTIONS)) {
            return TAG_TRANSACTIONS;
        }

        if (isFragmentVisible(fragmentManager, TAG_CATEGORIES)) {
            return TAG_CATEGORIES;
        }

        if (isFragmentVisible(fragmentManager, TAG_BUDGETS)) {
            return TAG_BUDGETS;
        }

        if (isFragmentVisible(fragmentManager, TAG_CHATBOT)) {
            return TAG_CHATBOT;
        }

        return null;
    }

    private boolean isFragmentVisible(
            FragmentManager fragmentManager,
            String tag
    ) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        return fragment != null && fragment.isVisible();
    }

    private void updateNavigation(String tag) {
        boolean overviewSelected = TAG_OVERVIEW.equals(tag);

        boolean financeSelected =
                TAG_TRANSACTIONS.equals(tag)
                        || TAG_CATEGORIES.equals(tag)
                        || TAG_BUDGETS.equals(tag);

        updateNavigationItem(
                iconOverview,
                labelOverview,
                overviewSelected
        );

        updateNavigationItem(
                iconFinance,
                labelFinance,
                financeSelected
        );

        updateNavigationItem(
                iconGroups,
                labelGroups,
                false
        );

        updateNavigationItem(
                iconChatbot,
                labelChatbot,
                TAG_CHATBOT.equals(tag)
        );
    }

    private void updateNavigationItem(
            ImageView icon,
            TextView label,
            boolean selected
    ) {
        int color = selected
                ? selectedColor
                : inactiveColor;

        icon.setColorFilter(color);
        label.setTextColor(color);

        label.setTypeface(
                selected
                        ? Typeface.DEFAULT_BOLD
                        : Typeface.DEFAULT
        );
    }

    private void updateSelectedNavigation() {
        selectedNavigationTag = getVisibleNavigationTag(
                getSupportFragmentManager()
        );

        if (selectedNavigationTag == null) {
            updateNavigation(TAG_OVERVIEW);
            return;
        }

        updateNavigation(selectedNavigationTag);
    }

    private void showNotImplementedMessage(String featureName) {
        Toast.makeText(
                this,
                featureName + " chưa được triển khai.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void openProfile() {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        startActivity(
                new Intent(
                        this,
                        ProfileActivity.class
                )
        );
    }

    private void openAddTransaction() {
        if (!sessionManager.isSessionActive()) {
            openLoginActivity();
            return;
        }

        transactionViewModel.resetTransactionAction();

        AddEditTransactionBottomSheet sheet =
                AddEditTransactionBottomSheet.newCreateInstance(
                        transactionViewModel
                );

        sheet.show(
                getSupportFragmentManager(),
                "AddEditTransactionBottomSheet"
        );
    }

    @Override
    protected void onDestroy() {
        sessionDisposables.clear();
        super.onDestroy();
    }
}
