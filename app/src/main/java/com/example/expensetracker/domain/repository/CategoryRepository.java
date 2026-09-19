package com.example.expensetracker.domain.repository;

import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.Category;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface CategoryRepository {

    Single<Resource<List<Category>>> getCategories();

    Single<Resource<Category>> createCategory(String name, int type, String icon, String color);

    Single<Resource<Category>> updateCategory(int id, String name, int type, String icon, String color);

    Single<Resource<Boolean>> deleteCategory(int id);
}