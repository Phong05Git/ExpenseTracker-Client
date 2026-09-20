package com.example.expensetracker.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.usecase.category.CreateCategoryUseCase;
import com.example.expensetracker.domain.usecase.category.DeleteCategoryUseCase;
import com.example.expensetracker.domain.usecase.category.GetCategoriesUseCase;
import com.example.expensetracker.domain.usecase.category.UpdateCategoryUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class CategoryViewModel extends ViewModel {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private Disposable loadRequest;
    private Disposable categoryActionRequest;
    private Disposable deleteActionRequest;

    private final MutableLiveData<Resource<List<Category>>> categories =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<Category>> categoryAction =
            new MutableLiveData<>();

    private final MutableLiveData<Resource<Boolean>> deleteAction =
            new MutableLiveData<>();

    @Inject
    public CategoryViewModel(
            GetCategoriesUseCase getCategoriesUseCase,
            CreateCategoryUseCase createCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase) {

        this.getCategoriesUseCase = getCategoriesUseCase;
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return categories;
    }

    public LiveData<Resource<Category>> getCategoryAction() {
        return categoryAction;
    }

    public LiveData<Resource<Boolean>> getDeleteAction() {
        return deleteAction;
    }

    public void loadCategories() {
        if (loadRequest != null && !loadRequest.isDisposed()) {
            return;
        }

        categories.setValue(Resource.loading());

        loadRequest = getCategoriesUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> categories.setValue(resource),
                        throwable -> categories.setValue(
                                Resource.<List<Category>>error(
                                        "Lỗi tải danh sách danh mục.")));

        disposables.add(loadRequest);
    }

    public void createCategory(
            String name,
            int type,
            String icon,
            String color) {

        if (categoryActionRequest != null) {
            categoryActionRequest.dispose();
        }

        categoryAction.setValue(Resource.loading());

        categoryActionRequest = createCategoryUseCase.execute(
                        name,
                        type,
                        icon,
                        color)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> categoryAction.setValue(resource),
                        throwable -> categoryAction.setValue(
                                Resource.<Category>error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi tạo danh mục."))));

        disposables.add(categoryActionRequest);
    }

    public void updateCategory(
            int id,
            String name,
            int type,
            String icon,
            String color) {

        if (categoryActionRequest != null) {
            categoryActionRequest.dispose();
        }

        categoryAction.setValue(Resource.loading());

        categoryActionRequest = updateCategoryUseCase.execute(
                        id,
                        name,
                        type,
                        icon,
                        color)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> categoryAction.setValue(resource),
                        throwable -> categoryAction.setValue(
                                Resource.<Category>error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi cập nhật danh mục."))));

        disposables.add(categoryActionRequest);
    }

    public void deleteCategory(int id) {
        if (deleteActionRequest != null) {
            deleteActionRequest.dispose();
        }

        deleteAction.setValue(Resource.loading());

        deleteActionRequest = deleteCategoryUseCase.execute(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resource -> deleteAction.setValue(resource),
                        throwable -> deleteAction.setValue(
                                Resource.<Boolean>error(com.example.expensetracker.util.ErrorUtils.getErrorMessage(throwable, "Lỗi xóa danh mục."))));

        disposables.add(deleteActionRequest);
    }

    public void resetCategoryAction() {
        categoryAction.setValue(Resource.idle());
    }

    public void resetDeleteAction() {
        deleteAction.setValue(Resource.idle());
    }

    @Override
    protected void onCleared() {
        disposables.clear();

        loadRequest = null;
        categoryActionRequest = null;
        deleteActionRequest = null;

        super.onCleared();
    }
}

