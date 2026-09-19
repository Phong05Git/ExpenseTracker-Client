package com.example.expensetracker.domain.usecase.category;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;
import com.example.expensetracker.domain.repository.CategoryRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public GetCategoriesUseCase(
            CategoryRepository categoryRepository) {
        this.categoryRepository =
                categoryRepository;
    }

    public Single<Resource<List<Category>>> execute() {
        return categoryRepository.getCategories();
    }
}