package com.example.expensetracker.data.prefs;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import com.example.expensetracker.domain.model.TokenData;
import com.example.expensetracker.security.CryptoManager;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class TokenManager {
    private static final Preferences.Key<String> ACCESS_TOKEN =
            PreferencesKeys.stringKey("access_token");

    private static final Preferences.Key<String> REFRESH_TOKEN =
            PreferencesKeys.stringKey("refresh_token");

    private static final Preferences.Key<String> ACCESS_TOKEN_EXPIRES_AT =
            PreferencesKeys.stringKey("access_token_expires_at");

    private static final Preferences.Key<String> REFRESH_TOKEN_EXPIRES_AT =
            PreferencesKeys.stringKey("refresh_token_expires_at");

    private final RxDataStore<Preferences> dataStore;
    private final CryptoManager cryptoManager;

    @Inject
    public TokenManager(
            RxDataStore<Preferences> dataStore,
            CryptoManager cryptoManager) {
        this.dataStore = dataStore;
        this.cryptoManager = cryptoManager;
    }

    public Single<TokenData> getTokenData() {
        return dataStore
                .data()
                .firstOrError()
                .map(this::toTokenData);
    }

    public String getAccessTokenBlocking() {
        try {
            return getTokenData()
                    .blockingGet()
                    .getAccessToken();
        } catch (Exception e) {
            return null;
        }
    }

    public String getRefreshTokenBlocking() {
        try {
            return getTokenData()
                    .blockingGet()
                    .getRefreshToken();
        } catch (Exception e) {
            return null;
        }
    }

    public Completable saveTokenData(TokenData tokenData) {
        return dataStore
                .updateDataAsync(preferences ->
                        Single.fromCallable(() -> {
                            MutablePreferences mutablePreferences =
                                    preferences.toMutablePreferences();

                            mutablePreferences.set(
                                    ACCESS_TOKEN,
                                    cryptoManager.encrypt(
                                            tokenData.getAccessToken()));

                            mutablePreferences.set(
                                    REFRESH_TOKEN,
                                    cryptoManager.encrypt(
                                            tokenData.getRefreshToken()));

                            mutablePreferences.set(
                                    ACCESS_TOKEN_EXPIRES_AT,
                                    cryptoManager.encrypt(
                                            tokenData.getAccessTokenExpiresAt()));

                            mutablePreferences.set(
                                    REFRESH_TOKEN_EXPIRES_AT,
                                    cryptoManager.encrypt(
                                            tokenData.getRefreshTokenExpiresAt()));

                            return mutablePreferences;
                        }))
                .ignoreElement();
    }

    public Completable clear() {
        return dataStore
                .updateDataAsync(preferences ->
                        Single.fromCallable(() -> {
                            MutablePreferences mutablePreferences =
                                    preferences.toMutablePreferences();

                            mutablePreferences.clear();

                            return mutablePreferences;
                        }))
                .ignoreElement();
    }

    private TokenData toTokenData(Preferences preferences) {
        String encryptedAccessToken = preferences.get(ACCESS_TOKEN);
        String encryptedRefreshToken = preferences.get(REFRESH_TOKEN);
        String encryptedAccessTokenExpiresAt = preferences.get(ACCESS_TOKEN_EXPIRES_AT);
        String encryptedRefreshTokenExpiresAt = preferences.get(REFRESH_TOKEN_EXPIRES_AT);

        if (encryptedAccessToken == null
                || encryptedRefreshToken == null
                || encryptedAccessTokenExpiresAt == null
                || encryptedRefreshTokenExpiresAt == null) {
            throw new IllegalStateException("Token storage is incomplete.");
        }

        return new TokenData(
                cryptoManager.decrypt(encryptedAccessToken),
                cryptoManager.decrypt(encryptedRefreshToken),
                cryptoManager.decrypt(encryptedAccessTokenExpiresAt),
                cryptoManager.decrypt(encryptedRefreshTokenExpiresAt));
    }
}