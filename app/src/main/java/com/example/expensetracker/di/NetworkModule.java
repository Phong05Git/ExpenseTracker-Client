package com.example.expensetracker.di;

import com.example.expensetracker.data.remote.api.AuthApi;
import com.example.expensetracker.data.remote.api.BudgetApi;
import com.example.expensetracker.data.remote.api.CategoryApi;
import com.example.expensetracker.data.remote.api.RefreshAuthApi;
import com.example.expensetracker.data.remote.api.ReportApi;
import com.example.expensetracker.data.remote.api.TransactionApi;
import com.example.expensetracker.data.remote.api.UserApi;
import com.example.expensetracker.data.remote.interceptor.AuthInterceptor;
import com.example.expensetracker.data.remote.interceptor.TokenAuthenticator;
import com.example.expensetracker.security.CertificatePinningHelper;
import com.example.expensetracker.util.Constants;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Module
@InstallIn(SingletonComponent.class)
public final class NetworkModule {

    private NetworkModule() {
    }

    @Provides
    @Singleton
    static CertificatePinner provideCertificatePinner(
            CertificatePinningHelper certificatePinningHelper) {
        return certificatePinningHelper.createCertificatePinner();
    }

    @Provides
    @Singleton
    @RefreshOkHttpClientQualifier
    static OkHttpClient provideRefreshOkHttpClient(
            CertificatePinner certificatePinner) {
        return new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    @RefreshAuthApiQualifier
    static RefreshAuthApi provideRefreshAuthApi(
            @RefreshOkHttpClientQualifier OkHttpClient refreshOkHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(refreshOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RefreshAuthApi.class);
    }

    @Provides
    @Singleton
    static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    @Provides
    @Singleton
    static OkHttpClient provideOkHttpClient(
            AuthInterceptor authInterceptor,
            TokenAuthenticator tokenAuthenticator,
            HttpLoggingInterceptor loggingInterceptor,
            CertificatePinner certificatePinner) {
        return new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .authenticator(tokenAuthenticator)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    static Retrofit provideRetrofit(
            OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    static AuthApi provideAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    static UserApi provideUserApi(Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }

    @Provides
    @Singleton
    static ReportApi provideReportsApi(Retrofit retrofit) {
        return retrofit.create(ReportApi.class);
    }

    @Provides
    @Singleton
    static TransactionApi provideTransactionApi(Retrofit retrofit) {
        return retrofit.create(TransactionApi.class);
    }

    @Provides
    @Singleton
    static CategoryApi provideCategoryApi(Retrofit retrofit) {
        return retrofit.create(CategoryApi.class);
    }

    @Provides
    @Singleton
    static BudgetApi provideBudgetApi(Retrofit retrofit) {
        return retrofit.create(BudgetApi.class);
    }
}