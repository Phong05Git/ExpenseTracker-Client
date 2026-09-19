package com.example.expensetracker.di;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public final class SecurityModule {
    private SecurityModule() {
    }

    @Provides
    @Singleton
    static RxDataStore<Preferences> provideTokenDataStore(
            @ApplicationContext Context context) {
        return new RxPreferenceDataStoreBuilder(
                context,
                "secure_tokens")
                .setIoScheduler(Schedulers.io())
                .build();
    }
}