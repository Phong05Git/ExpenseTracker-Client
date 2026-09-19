package com.example.expensetracker.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.domain.model.TokenData;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class SplashActivity extends AppCompatActivity {

    @Inject
    TokenManager tokenManager;

    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkSession();
    }

    private void checkSession() {
        new Thread(() -> {
            boolean hasSession = false;

            try {
                TokenData tokenData =
                        tokenManager.getTokenData().blockingGet();

                hasSession = tokenData.getAccessToken() != null
                        && !tokenData.getAccessToken().isBlank()
                        && tokenData.getRefreshToken() != null
                        && !tokenData.getRefreshToken().isBlank();
            } catch (Exception ignored) {
            }

            boolean finalHasSession = hasSession;

            runOnUiThread(() -> {
                if (finalHasSession) {
                    openMainActivity();
                } else {
                    openLoginActivity();
                }
            });
        }).start();
    }

    private void openMainActivity() {
        sessionManager.reset();

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}