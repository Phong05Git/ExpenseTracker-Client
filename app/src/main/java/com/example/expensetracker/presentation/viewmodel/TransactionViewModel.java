package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.usecase.transaction.CreateTransactionUseCase;
import com.example.expensetracker.domain.usecase.transaction.DeleteTransactionUseCase;
import com.example.expensetracker.domain.usecase.transaction.FilterTransactionsUseCase;
import com.example.expensetracker.domain.usecase.transaction.GetTransactionsByDateUseCase;
import com.example.expensetracker.domain.usecase.transaction.UpdateTransactionUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class TransactionViewModel extends ViewModel {

    private final FilterTransactionsUseCase filterTransactionsUseCase;
    private final GetTransactionsByDateUseCase getTransactionsByDateUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private Disposable monthRequest;
    private Disposable dateRequest;
    private Disposable filterRequest;
    private Disposable transactionActionRequest;
    private Disposable deleteActionRequest;

    private final MutableLiveData<Resource<List<Transaction>>> transactions =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<List<Transaction>>> monthTransactions =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<List<Transaction>>> filteredTransactions =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<Transaction>> transactionAction =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<Boolean>> deleteAction =
            new MutableLiveData<>();

    @Inject
    public TransactionViewModel(
            FilterTransactionsUseCase filterTransactionsUseCase,
            GetTransactionsByDateUseCase getTransactionsByDateUseCase,
            CreateTransactionUseCase createTransactionUseCase,
            UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase) {

        this.filterTransactionsUseCase =
                filterTransactionsUseCase;

        this.getTransactionsByDateUseCase =
                getTransactionsByDateUseCase;

        this.createTransactionUseCase =
                createTransactionUseCase;

        this.updateTransactionUseCase =
                updateTransactionUseCase;

        this.deleteTransactionUseCase =
                deleteTransactionUseCase;
    }

    public LiveData<Resource<List<Transaction>>> getTransactions() {
        return transactions;
    }

    public LiveData<Resource<List<Transaction>>> getMonthTransactions() {
        return monthTransactions;
    }

    public LiveData<Resource<List<Transaction>>> getFilteredTransactions() {
        return filteredTransactions;
    }

    public LiveData<Resource<Transaction>> getTransactionAction() {
        return transactionAction;
    }

    public LiveData<Resource<Boolean>> getDeleteAction() {
        return deleteAction;
    }

    public void loadMonth(
            String fromDate,
            String toDate) {

        if (monthRequest != null) {
            monthRequest.dispose();
        }

        monthTransactions.setValue(
                Resource.loading());

        monthRequest =
                filterTransactionsUseCase
                        .execute(
                                fromDate,
                                toDate,
                                null,
                                null,
                                null,
                                1,
                                100)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    monthTransactions.setValue(
                                            resource);
                                },
                                throwable -> {
                                    monthTransactions.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi tải dữ liệu lịch.")));
                                });

        disposables.add(monthRequest);
    }

    public void filterTransactions(
            String fromDate,
            String toDate,
            Integer categoryId,
            Integer type,
            String keyword) {

        if (filterRequest != null) {
            filterRequest.dispose();
        }

        filteredTransactions.setValue(
                Resource.loading());

        filterRequest =
                filterTransactionsUseCase
                        .execute(
                                fromDate,
                                toDate,
                                categoryId,
                                type,
                                keyword,
                                1,
                                100)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    filteredTransactions.setValue(
                                            resource);
                                },
                                throwable -> {
                                    filteredTransactions.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi lọc giao dịch.")));
                                });

        disposables.add(filterRequest);
    }

    public void loadByDate(
            String date) {

        if (dateRequest != null) {
            dateRequest.dispose();
        }

        transactions.setValue(
                Resource.loading());

        dateRequest =
                getTransactionsByDateUseCase
                        .execute(date)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    transactions.setValue(
                                            resource);
                                },
                                throwable -> {
                                    transactions.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi tải giao dịch trong ngày.")));
                                });

        disposables.add(dateRequest);
    }

    public void createTransaction(
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate,
            String source) {

        if (transactionActionRequest != null) {
            transactionActionRequest.dispose();
        }

        transactionAction.setValue(
                Resource.loading());

        transactionActionRequest =
                createTransactionUseCase
                        .execute(
                                categoryId,
                                amount,
                                type,
                                note,
                                transactionDate,
                                source)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    transactionAction.setValue(
                                            resource);
                                },
                                throwable -> {
                                    transactionAction.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi thêm giao dịch.")));
                                });

        disposables.add(
                transactionActionRequest);
    }

    public void updateTransaction(
            int id,
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate) {

        if (transactionActionRequest != null) {
            transactionActionRequest.dispose();
        }

        transactionAction.setValue(
                Resource.loading());

        transactionActionRequest =
                updateTransactionUseCase
                        .execute(
                                id,
                                categoryId,
                                amount,
                                type,
                                note,
                                transactionDate)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    transactionAction.setValue(
                                            resource);
                                },
                                throwable -> {
                                    transactionAction.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi cập nhật giao dịch.")));
                                });

        disposables.add(
                transactionActionRequest);
    }

    public void deleteTransaction(
            int id) {

        if (deleteActionRequest != null) {
            deleteActionRequest.dispose();
        }

        deleteAction.setValue(
                Resource.loading());

        deleteActionRequest =
                deleteTransactionUseCase
                        .execute(id)
                        .subscribeOn(
                                Schedulers.io())
                        .observeOn(
                                AndroidSchedulers.mainThread())
                        .subscribe(
                                resource -> {
                                    deleteAction.setValue(
                                            resource);
                                },
                                throwable -> {
                                    deleteAction.setValue(
                                            Resource.error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi xóa giao dịch.")));
                                });

        disposables.add(
                deleteActionRequest);
    }

    public void resetTransactionAction() {
        transactionAction.setValue(
                Resource.idle());
    }

    public void resetDeleteAction() {
        deleteAction.setValue(
                Resource.idle());
    }

    @Override
    protected void onCleared() {

        disposables.clear();

        monthRequest = null;
        dateRequest = null;
        filterRequest = null;
        transactionActionRequest = null;
        deleteActionRequest = null;

        super.onCleared();
    }
}
