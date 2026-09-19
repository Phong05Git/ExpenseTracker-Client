package com.example.expensetracker.security;

import android.os.Handler;
import android.os.Looper;

import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.session.SessionManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class IdleTimeoutManager {
    private static final long IDLE_TIMEOUT_MILLIS = 15 * 60 * 1000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable;

    private boolean monitoring;

    @Inject
    public IdleTimeoutManager(
            TokenManager tokenManager,
            SessionManager sessionManager) {
        timeoutRunnable = () -> {
            monitoring = false;

            new Thread(() -> {
                try {
                    tokenManager.clear().blockingAwait();
                } catch (Exception ignored) {
                }

                sessionManager.notifySessionExpired();
            }).start();
        };
    }

    public synchronized void start() {
        monitoring = true;
        reset();
    }

    public synchronized void stop() {
        monitoring = false;
        handler.removeCallbacks(timeoutRunnable);
    }

    public synchronized void reset() {
        if (!monitoring) {
            return;
        }

        handler.removeCallbacks(timeoutRunnable);
        handler.postDelayed(timeoutRunnable, IDLE_TIMEOUT_MILLIS);
    }
}