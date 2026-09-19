package com.example.expensetracker.security;

import android.app.Activity;
import android.view.WindowManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ScreenSecurityHelper {

    @Inject
    public ScreenSecurityHelper() {
    }

    public void enable(Activity activity) {
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    public void disable(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}