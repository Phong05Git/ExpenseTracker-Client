package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.domain.usecase.budget.CreateBudgetUseCase;
import com.example.expensetracker.domain.usecase.budget.DeleteBudgetUseCase;
import com.example.expensetracker.domain.usecase.budget.GetBudgetsUseCase;
import com.example.expensetracker.domain.usecase.budget.UpdateBudgetUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class BudgetViewModel extends ViewModel {

    private final GetBudgetsUseCase getBudgetsUseCase;
    private final CreateBudgetUseCase createBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private Disposable loadRequest;
    private Disposable budgetActionRequest;
    private Disposable deleteActionRequest;

    private final MutableLiveData<Resource<List<Budget>>> budgets = new MutableLiveData<>();
    private final MutableLiveData<Resource<Budget>> budgetAction = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> deleteAction = new MutableLiveData<>();

    @Inject
    public BudgetViewModel(
            GetBudgetsUseCase getBudgetsUseCase,
            CreateBudgetUseCase createBudgetUseCase,
            UpdateBudgetUseCase updateBudgetUseCase,
            DeleteBudgetUseCase deleteBudgetUseCase) {
        this.getBudgetsUseCase = getBudgetsUseCase;
        this.createBudgetUseCase = createBudgetUseCase;
        this.updateBudgetUseCase = updateBudgetUseCase;
        this.deleteBudgetUseCase = deleteBudgetUseCase;
    }

    public LiveData<Resource<List<Budget>>> getBudgets() {
        return budgets;
    }

    public LiveData<Resource<Budget>> getBudgetAction() {
        return budgetAction;
    }

    public LiveData<Resource<Boolean>> getDeleteAction() {
        return deleteAction;
    }

    public void loadBudgets() {
        if (loadRequest != null) loadRequest.dispose();

        budgets.setValue(Resource.loading());

        loadRequest = getBudgetsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> budgets.setValue(resource),
                        throwable -> budgets.setValue(
                                Resource.error("Lỗi tải danh sách ngân sách.")));

        disposables.add(loadRequest);
    }

    public void createBudget(int categoryId, double limitAmount, int month, int year) {
        if (budgetActionRequest != null) budgetActionRequest.dispose();

        budgetAction.setValue(Resource.loading());

        budgetActionRequest = createBudgetUseCase.execute(
                        categoryId,
                        limitAmount,
                        month,
                        year)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> budgetAction.setValue(resource),
                        throwable -> budgetAction.setValue(
                                Resource.error("Lỗi tạo ngân sách.")));

        disposables.add(budgetActionRequest);
    }

    public void updateBudget(int id, int categoryId, double limitAmount, int month, int year) {
        if (budgetActionRequest != null) budgetActionRequest.dispose();

        budgetAction.setValue(Resource.loading());

        budgetActionRequest = updateBudgetUseCase.execute(
                        id,
                        categoryId,
                        limitAmount,
                        month,
                        year)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> budgetAction.setValue(resource),
                        throwable -> budgetAction.setValue(
                                Resource.error("Lỗi cập nhật ngân sách.")));

        disposables.add(budgetActionRequest);
    }

    public void deleteBudget(int id) {
        if (deleteActionRequest != null) deleteActionRequest.dispose();

        deleteAction.setValue(Resource.loading());

        deleteActionRequest = deleteBudgetUseCase.execute(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> deleteAction.setValue(resource),
                        throwable -> deleteAction.setValue(
                                Resource.error("Lỗi xóa ngân sách.")));

        disposables.add(deleteActionRequest);
    }

    public void resetBudgetAction() {
        budgetAction.setValue(Resource.idle());
    }

    public void resetDeleteAction() {
        deleteAction.setValue(Resource.idle());
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        loadRequest = null;
        budgetActionRequest = null;
        deleteActionRequest = null;
        super.onCleared();
    }
}