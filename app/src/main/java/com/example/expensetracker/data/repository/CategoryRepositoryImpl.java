package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.remote.api.CategoryApi;
import com.example.expensetracker.data.remote.dto.categories.CategoryDto;
import com.example.expensetracker.data.remote.dto.categories.CreateCategoryRequestDto;
import com.example.expensetracker.data.remote.dto.categories.UpdateCategoryRequestDto;
import com.example.expensetracker.data.remote.dto.common.ApiResponseDto;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryApi categoryApi;

    @Inject
    public CategoryRepositoryImpl(CategoryApi categoryApi) {
        this.categoryApi = categoryApi;
    }

    @Override
    public Single<Resource<List<Category>>> getCategories() {
        return Single.fromCallable(() -> {
            Response<ApiResponseDto<List<CategoryDto>>> response = categoryApi.getCategories().execute();

            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().getData() != null) {

                List<Category> result = new ArrayList<>();

                for (CategoryDto dto : response.body().getData()) {
                    result.add(toCategory(dto));
                }

                return Resource.success(result);
            }

            return Resource.<List<Category>>error("Không thể tải danh mục.");
        }).onErrorReturnItem(
                Resource.<List<Category>>error("Lỗi kết nối máy chủ.")
        );
    }

    @Override
    public Single<Resource<Category>> createCategory(String name, int type, String icon, String color) {
        return Single.fromCallable(() -> {
            CreateCategoryRequestDto request = new CreateCategoryRequestDto(name, type, icon, color);
            Response<ApiResponseDto<CategoryDto>> response = categoryApi.createCategory(request).execute();

            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().getData() != null) {

                return Resource.success(toCategory(response.body().getData()));
            }

            return Resource.<Category>error(
                    getErrorMessage(response, "Không thể tạo danh mục.")
            );
        }).onErrorReturnItem(
                Resource.<Category>error("Lỗi kết nối máy chủ.")
        );
    }

    @Override
    public Single<Resource<Category>> updateCategory(
            int id,
            String name,
            int type,
            String icon,
            String color) {

        return Single.fromCallable(() -> {
            UpdateCategoryRequestDto request =
                    new UpdateCategoryRequestDto(name, type, icon, color);

            Response<ApiResponseDto<CategoryDto>> response =
                    categoryApi.updateCategory(id, request).execute();

            if (response.isSuccessful()
                    && response.body() != null
                    && response.body().getData() != null) {

                return Resource.success(toCategory(response.body().getData()));
            }

            return Resource.<Category>error(
                    getErrorMessage(response, "Không thể cập nhật danh mục.")
            );
        }).onErrorReturnItem(
                Resource.<Category>error("Lỗi kết nối máy chủ.")
        );
    }

    @Override
    public Single<Resource<Boolean>> deleteCategory(int id) {
        return Single.fromCallable(() -> {
            Response<Void> response = categoryApi.deleteCategory(id).execute();

            if (response.isSuccessful()) {
                return Resource.success(true);
            }

            if (response.code() == 403) {
                return Resource.<Boolean>error(
                        "Bạn không có quyền xóa danh mục này."
                );
            }

            if (response.code() == 404) {
                return Resource.<Boolean>error(
                        "Không tìm thấy danh mục."
                );
            }

            if (response.code() == 409) {
                return Resource.<Boolean>error(
                        "Không thể xóa danh mục này vì đang được sử dụng bởi giao dịch."
                );
            }

            return Resource.<Boolean>error(
                    "Không thể xóa danh mục."
            );
        }).onErrorReturnItem(
                Resource.<Boolean>error("Lỗi kết nối máy chủ.")
        );
    }

    private Category toCategory(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getUserId(),
                dto.getName(),
                dto.getType(),
                dto.getIcon(),
                dto.getColor()
        );
    }

    private String getErrorMessage(Response<?> response, String defaultMessage) {
        if (response.code() == 403) {
            return "Bạn không có quyền thực hiện thao tác này.";
        }

        if (response.code() == 404) {
            return "Không tìm thấy danh mục.";
        }

        if (response.code() == 409) {
            return "Tên danh mục đã tồn tại hoặc thao tác không hợp lệ.";
        }

        return defaultMessage;
    }
}