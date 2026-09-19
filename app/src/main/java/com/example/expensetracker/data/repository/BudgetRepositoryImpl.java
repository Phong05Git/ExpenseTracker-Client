package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.remote.api.BudgetApi;
import com.example.expensetracker.data.remote.dto.budgets.BudgetDto;
import com.example.expensetracker.data.remote.dto.budgets.CreateBudgetRequestDto;
import com.example.expensetracker.data.remote.dto.budgets.UpdateBudgetRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Budget;
import com.example.expensetracker.domain.repository.BudgetRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class BudgetRepositoryImpl implements BudgetRepository {

    private final BudgetApi budgetApi;

    @Inject
    public BudgetRepositoryImpl(BudgetApi budgetApi) {
        this.budgetApi = budgetApi;
    }

    @Override
    public Single<Resource<List<Budget>>> getBudgets() {
        return Single.create(emitter -> {
            try {
                Response<ApiResponseDto<List<BudgetDto>>> response = budgetApi.getBudgets().execute();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Budget> result = new ArrayList<>();

                    for (BudgetDto dto : response.body().getData()) {
                        result.add(toBudget(dto));
                    }

                    emitter.onSuccess(Resource.success(result));
                    return;
                }

                emitter.onSuccess(Resource.<List<Budget>>error("Không thể tải danh sách ngân sách."));
            } catch (Exception e) {
                emitter.onSuccess(Resource.<List<Budget>>error("Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Budget>> createBudget(int categoryId, double limitAmount, int month, int year) {
        return Single.create(emitter -> {
            try {
                CreateBudgetRequestDto request = new CreateBudgetRequestDto(categoryId, limitAmount, month, year);
                Response<ApiResponseDto<BudgetDto>> response = budgetApi.createBudget(request).execute();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    emitter.onSuccess(Resource.success(toBudget(response.body().getData())));
                    return;
                }

                if (response.code() == 403) {
                    emitter.onSuccess(Resource.<Budget>error("Bạn không có quyền sử dụng danh mục này."));
                    return;
                }

                if (response.code() == 409) {
                    emitter.onSuccess(Resource.<Budget>error("Ngân sách cho danh mục và kỳ này đã tồn tại."));
                    return;
                }

                emitter.onSuccess(Resource.<Budget>error("Không thể tạo ngân sách."));
            } catch (Exception e) {
                emitter.onSuccess(Resource.<Budget>error("Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Budget>> updateBudget(int id, int categoryId, double limitAmount, int month, int year) {
        return Single.create(emitter -> {
            try {
                UpdateBudgetRequestDto request = new UpdateBudgetRequestDto(categoryId, limitAmount, month, year);
                Response<ApiResponseDto<BudgetDto>> response = budgetApi.updateBudget(id, request).execute();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    emitter.onSuccess(Resource.success(toBudget(response.body().getData())));
                    return;
                }

                if (response.code() == 403) {
                    emitter.onSuccess(Resource.<Budget>error("Bạn không có quyền sử dụng danh mục này."));
                    return;
                }

                if (response.code() == 404) {
                    emitter.onSuccess(Resource.<Budget>error("Không tìm thấy ngân sách."));
                    return;
                }

                if (response.code() == 409) {
                    emitter.onSuccess(Resource.<Budget>error("Ngân sách cho danh mục và kỳ này đã tồn tại."));
                    return;
                }

                emitter.onSuccess(Resource.<Budget>error("Không thể cập nhật ngân sách."));
            } catch (Exception e) {
                emitter.onSuccess(Resource.<Budget>error("Lỗi kết nối máy chủ."));
            }
        });
    }

    @Override
    public Single<Resource<Boolean>> deleteBudget(int id) {
        return Single.create(emitter -> {
            try {
                Response<Void> response = budgetApi.deleteBudget(id).execute();

                if (response.isSuccessful()) {
                    emitter.onSuccess(Resource.success(true));
                    return;
                }

                if (response.code() == 404) {
                    emitter.onSuccess(Resource.<Boolean>error("Không tìm thấy ngân sách."));
                    return;
                }

                if (response.code() == 403) {
                    emitter.onSuccess(Resource.<Boolean>error("Bạn không có quyền xóa ngân sách này."));
                    return;
                }

                emitter.onSuccess(Resource.<Boolean>error("Không thể xóa ngân sách."));
            } catch (Exception e) {
                emitter.onSuccess(Resource.<Boolean>error("Lỗi kết nối máy chủ."));
            }
        });
    }

    private Budget toBudget(BudgetDto dto) {
        return new Budget(
                dto.getId(),
                dto.getCategoryId(),
                dto.getCategoryName(),
                dto.getCategoryIcon(),
                dto.getCategoryColor(),
                dto.getLimitAmount(),
                dto.getSpentAmount(),
                dto.getRemainingAmount(),
                dto.getMonth(),
                dto.getYear());
    }
}