package com.example.expensetracker.security;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.domain.model.TokenData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TokenManagerTest {
    private static TokenManager tokenManager;

    @BeforeClass
    public static void setUpClass() {
        Context context =
                ApplicationProvider.getApplicationContext();

        RxDataStore<Preferences> dataStore =
                new RxPreferenceDataStoreBuilder(
                        context,
                        "token_manager_test")
                        .build();

        CryptoManager cryptoManager =
                new CryptoManager(context);

        tokenManager = new TokenManager(
                dataStore,
                cryptoManager);
    }

    @Before
    public void setUp() {
        tokenManager
                .clear()
                .blockingAwait();
    }

    @Test
    public void saveAndRead_shouldReturnSameTokenData() {
        TokenData original =
                new TokenData(
                        "access-token-test",
                        "refresh-token-test",
                        "2026-09-01T10:00:00Z",
                        "2026-10-01T10:00:00Z");

        tokenManager
                .saveTokenData(original)
                .blockingAwait();

        TokenData restored =
                tokenManager
                        .getTokenData()
                        .blockingGet();

        Assert.assertEquals(
                original.getAccessToken(),
                restored.getAccessToken());

        Assert.assertEquals(
                original.getRefreshToken(),
                restored.getRefreshToken());

        Assert.assertEquals(
                original.getAccessTokenExpiresAt(),
                restored.getAccessTokenExpiresAt());

        Assert.assertEquals(
                original.getRefreshTokenExpiresAt(),
                restored.getRefreshTokenExpiresAt());
    }

    @Test
    public void clear_shouldRemoveTokenData() {
        TokenData tokenData =
                new TokenData(
                        "access-token-test",
                        "refresh-token-test",
                        "2026-09-01T10:00:00Z",
                        "2026-10-01T10:00:00Z");

        tokenManager
                .saveTokenData(tokenData)
                .blockingAwait();

        tokenManager
                .clear()
                .blockingAwait();

        try {
            tokenManager
                    .getTokenData()
                    .blockingGet();

            Assert.fail(
                    "Token data should not exist after clear.");
        } catch (IllegalStateException expected) {
            Assert.assertEquals(
                    "Token storage is incomplete.",
                    expected.getMessage());
        }
    }
}