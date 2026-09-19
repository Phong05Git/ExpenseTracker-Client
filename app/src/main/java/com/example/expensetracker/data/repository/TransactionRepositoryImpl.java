package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.remote.api.TransactionApi;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.data.remote.dto.common.PagedResultDto;
import com.example.expensetracker.data.remote.dto.transactions.CreateTransactionRequestDto;
import com.example.expensetracker.data.remote.dto.transactions.TransactionDto;
import com.example.expensetracker.data.remote.dto.transactions.UpdateTransactionRequestDto;
import com.example.expensetracker.data.remote.mapper.TransactionMapper;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Transaction;
import com.example.expensetracker.domain.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class TransactionRepositoryImpl
        implements TransactionRepository {

    private final TransactionApi transactionApi;
    private final TransactionMapper transactionMapper;

    @Inject
    public TransactionRepositoryImpl(
            TransactionApi transactionApi,
            TransactionMapper transactionMapper) {
        this.transactionApi = transactionApi;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Single<Resource<List<Transaction>>> getTransactions(
            String fromDate,
            String toDate,
            Integer categoryId,
            Integer type,
            String keyword,
            int page,
            int pageSize) {

        return Single.create(emitter -> {
            try {
                Response<ApiResponseDto<PagedResultDto<TransactionDto>>> response =
                        transactionApi.getTransactions(
                                fromDate,
                                toDate,
                                categoryId,
                                type,
                                keyword,
                                page,
                                pageSize).execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    PagedResultDto<TransactionDto> data =
                            response.body().getData();

                    List<Transaction> result =
                            new ArrayList<>();

                    if (data.getItems() != null) {
                        for (TransactionDto dto :
                                data.getItems()) {
                            result.add(
                                    transactionMapper.map(dto));
                        }
                    }

                    emitter.onSuccess(
                            Resource.success(result));
                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể tải danh sách giao dịch."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<List<Transaction>>> getTransactionsByDate(
            String date) {

        return Single.create(emitter -> {
            try {
                Response<ApiResponseDto<List<TransactionDto>>> response =
                        transactionApi
                                .getTransactionsByDate(date)
                                .execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    List<Transaction> result =
                            new ArrayList<>();

                    for (TransactionDto dto :
                            response.body().getData()) {
                        result.add(
                                transactionMapper.map(dto));
                    }

                    emitter.onSuccess(
                            Resource.success(result));
                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể tải giao dịch trong ngày."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Transaction>> createTransaction(
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate,
            String source) {

        return Single.create(emitter -> {
            try {
                CreateTransactionRequestDto request =
                        new CreateTransactionRequestDto(
                                categoryId,
                                amount,
                                type,
                                note,
                                transactionDate,
                                source);

                Response<ApiResponseDto<TransactionDto>> response =
                        transactionApi
                                .createTransaction(request)
                                .execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    emitter.onSuccess(
                            Resource.success(
                                    transactionMapper.map(
                                            response.body().getData())));
                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể thêm giao dịch."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Transaction>> updateTransaction(
            int id,
            int categoryId,
            double amount,
            int type,
            String note,
            String transactionDate) {

        return Single.create(emitter -> {
            try {
                UpdateTransactionRequestDto request =
                        new UpdateTransactionRequestDto(
                                categoryId,
                                amount,
                                type,
                                note,
                                transactionDate);

                Response<ApiResponseDto<TransactionDto>> response =
                        transactionApi
                                .updateTransaction(
                                        id,
                                        request)
                                .execute();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null) {

                    emitter.onSuccess(
                            Resource.success(
                                    transactionMapper.map(
                                            response.body().getData())));
                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể cập nhật giao dịch."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Boolean>> deleteTransaction(
            int id) {

        return Single.create(emitter -> {
            try {
                Response<Void> response =
                        transactionApi
                                .deleteTransaction(id)
                                .execute();

                if (response.isSuccessful()) {
                    emitter.onSuccess(
                            Resource.success(true));
                    return;
                }

                emitter.onSuccess(
                        Resource.error(
                                "Không thể xóa giao dịch."));
            } catch (Exception e) {
                emitter.onSuccess(
                        Resource.error(
                                "Lỗi kết nối máy chủ."));
            }
        });
    }
}