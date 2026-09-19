package com.example.expensetracker.di;

import com.example.expensetracker.data.repository.AuthRepositoryImpl;
import com.example.expensetracker.data.repository.BudgetRepositoryImpl;
import com.example.expensetracker.data.repository.CategoryRepositoryImpl;
import com.example.expensetracker.data.repository.ReportRepositoryImpl;
import com.example.expensetracker.data.repository.UserRepositoryImpl;
import com.example.expensetracker.domain.repository.AuthRepository;
import com.example.expensetracker.domain.repository.BudgetRepository;
import com.example.expensetracker.domain.repository.CategoryRepository;
import com.example.expensetracker.domain.repository.ReportRepository;
import com.example.expensetracker.domain.repository.UserRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;
import com.example.expensetracker.data.repository.TransactionRepositoryImpl;
import com.example.expensetracker.domain.repository.TransactionRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    private RepositoryModule() {
    }

    @Binds
    @Singleton
    abstract AuthRepository bindAuthRepository(
            AuthRepositoryImpl implementation);

    @Binds
    @Singleton
    abstract UserRepository bindUserRepository(
            UserRepositoryImpl implementation);

    @Binds
    @Singleton
    public abstract ReportRepository bindReportRepository(
            ReportRepositoryImpl implementation);

    @Binds
    @Singleton
    public abstract TransactionRepository bindTransactionRepository(
            TransactionRepositoryImpl implementation);

    @Binds
    @Singleton
    abstract CategoryRepository bindCategoryRepository(
            CategoryRepositoryImpl implementation);

    @Binds
    @Singleton
    abstract BudgetRepository bindBudgetRepository(
            BudgetRepositoryImpl implementation);
}