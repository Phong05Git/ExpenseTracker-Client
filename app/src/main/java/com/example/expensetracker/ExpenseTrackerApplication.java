package com.example.expensetracker;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.WindowManager;

import com.example.expensetracker.data.prefs.TokenManager;
import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.security.DebuggerDetectionHelper;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class ExpenseTrackerApplication extends Application {

    private static final String TAG = "ExpenseTrackerSecurity";
    private static final long DEBUGGER_CHECK_INTERVAL_MILLIS = 10000L;

    @Inject
    TokenManager tokenManager;

    @Inject
    SessionManager sessionManager;

    @Inject
    DebuggerDetectionHelper debuggerDetectionHelper;

    private final Handler securityHandler =
            new Handler(Looper.getMainLooper());

    private Activity currentActivity;
    private boolean securityMonitoring;

    private final ActivityLifecycleCallbacks activityLifecycleCallbacks =
            new ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(
                        Activity activity,
                        Bundle savedInstanceState) {
                    activity.getWindow().addFlags(
                            WindowManager.LayoutParams.FLAG_SECURE);
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    currentActivity = activity;
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    currentActivity = activity;
                    startSecurityMonitoring();
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (currentActivity == activity) {
                        stopSecurityMonitoring();
                    }
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    if (currentActivity == activity) {
                        currentActivity = null;
                    }
                }

                @Override
                public void onActivitySaveInstanceState(
                        Activity activity,
                        Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (currentActivity == activity) {
                        currentActivity = null;
                    }
                }
            };

    private final Runnable securityCheckRunnable = new Runnable() {

        @Override
        public void run() {
            if (!securityMonitoring) {
                return;
            }

            if ("debug".equals(BuildConfig.BUILD_TYPE)) {
                securityHandler.postDelayed(
                        this,
                        DEBUGGER_CHECK_INTERVAL_MILLIS);
                return;
            }

            if (debuggerDetectionHelper.isDebuggerConnected()) {
                handleDebuggerDetected();
                return;
            }

            securityHandler.postDelayed(
                    this,
                    DEBUGGER_CHECK_INTERVAL_MILLIS);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(
                activityLifecycleCallbacks);
    }

    private synchronized void startSecurityMonitoring() {
        if (securityMonitoring) {
            return;
        }

        securityMonitoring = true;

        securityHandler.removeCallbacks(
                securityCheckRunnable);

        securityHandler.post(
                securityCheckRunnable);
    }

    private synchronized void stopSecurityMonitoring() {
        securityMonitoring = false;

        securityHandler.removeCallbacks(
                securityCheckRunnable);
    }

    private void handleDebuggerDetected() {
        stopSecurityMonitoring();

        if ("securityTest".equals(BuildConfig.BUILD_TYPE)) {
            Log.w(
                    TAG,
                    "Debugger detected in security test build.");
        }

        new Thread(() -> {
            try {
                tokenManager.clear().blockingAwait();
            } catch (Exception ignored) {
            }

            sessionManager.invalidateSession();

            Activity activity = currentActivity;

            if (activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(() -> {
                    if (!activity.isFinishing()) {
                        activity.finishAndRemoveTask();
                    }
                });
            }

            Process.killProcess(Process.myPid());
        }).start();
    }

    @Override
    public void onTerminate() {
        stopSecurityMonitoring();

        unregisterActivityLifecycleCallbacks(
                activityLifecycleCallbacks);

        super.onTerminate();
    }
}