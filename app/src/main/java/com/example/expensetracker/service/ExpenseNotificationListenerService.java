package com.example.expensetracker.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.expensetracker.BuildConfig;
import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.usecase.category.GetCategoriesUseCase;
import com.example.expensetracker.domain.usecase.transaction.CreateTransactionUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public final class ExpenseNotificationListenerService
        extends NotificationListenerService {

    private static final String TAG = "ExpenseNotification";
    private static final String INCOME_CATEGORY = "Thu nhập khác";
    private static final String EXPENSE_CATEGORY = "Chi phí khác";
    private static final int INCOME_TYPE = 1;
    private static final int EXPENSE_TYPE = 2;

    @Inject CreateTransactionUseCase createTransactionUseCase;
    @Inject GetCategoriesUseCase getCategoriesUseCase;
    @Inject TokenManager tokenManager;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if (statusBarNotification == null) return;

        String packageName = statusBarNotification.getPackageName();

        NotificationTransaction notificationTransaction =
                NotificationParser.parse(
                        packageName,
                        statusBarNotification.getNotification());

        if (notificationTransaction == null) return;

        if (!hasActiveSession()) {
            return;
        }

        createTransaction(notificationTransaction);
    }

    private boolean hasActiveSession() {
        String accessToken = tokenManager.getAccessTokenBlocking();
        String refreshToken = tokenManager.getRefreshTokenBlocking();

        return accessToken != null
                && !accessToken.isBlank()
                && refreshToken != null
                && !refreshToken.isBlank();
    }

    private void createTransaction(
            NotificationTransaction notificationTransaction) {

        disposables.add(
                getCategoriesUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                resource ->
                                        handleCategories(
                                                resource,
                                                notificationTransaction),
                                throwable -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.e(
                                                TAG,
                                                "Không thể tải danh mục.",
                                                throwable);
                                    }
                                }));
    }

    private void handleCategories(
            Resource<List<Category>> resource,
            NotificationTransaction notificationTransaction) {

        if (resource == null
                || resource.getStatus() != Resource.Status.SUCCESS
                || resource.getData() == null) {

            if (BuildConfig.DEBUG) {
                Log.w(
                        TAG,
                        "Không thể lấy danh mục để tạo giao dịch.");
            }

            return;
        }

        int categoryId = findDefaultCategoryId(
                resource.getData(),
                notificationTransaction.getType());

        if (categoryId <= 0) {
            if (BuildConfig.DEBUG) {
                Log.w(
                        TAG,
                        "Không tìm thấy danh mục mặc định.");
            }

            return;
        }

        disposables.add(
                createTransactionUseCase.execute(
                                categoryId,
                                notificationTransaction.getAmount(),
                                notificationTransaction.getType(),
                                notificationTransaction.getNote(),
                                notificationTransaction.getTransactionDate(),
                                notificationTransaction.getSource())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleCreateResult,
                                throwable -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.e(
                                                TAG,
                                                "Không thể tạo giao dịch từ notification.",
                                                throwable);
                                    }
                                }));
    }

    private int findDefaultCategoryId(
            List<Category> categories,
            int type) {

        String categoryName =
                type == INCOME_TYPE
                        ? INCOME_CATEGORY
                        : EXPENSE_CATEGORY;

        for (Category category : categories) {
            if (category == null) continue;
            if (category.getUserId() != null) continue;
            if (category.getType() != type) continue;
            if (categoryName.equals(category.getName())) {
                return category.getId();
            }
        }

        return -1;
    }

    private void handleCreateResult(
            Resource<Transaction> resource) {

        if (resource == null) return;

        if (resource.getStatus() == Resource.Status.SUCCESS) {
            if (BuildConfig.DEBUG) {
                Log.d(
                        TAG,
                        "Đã tự động tạo giao dịch từ notification.");
            }

            return;
        }

        if (resource.getStatus() == Resource.Status.ERROR) {
            if (BuildConfig.DEBUG) {
                Log.e(
                        TAG,
                        "Tạo giao dịch thất bại.");
            }
        }
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}