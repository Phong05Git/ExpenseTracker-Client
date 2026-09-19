package com.example.expensetracker.security;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Debug;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class DebuggerDetectionHelper {

    private final Application application;

    @Inject
    public DebuggerDetectionHelper(Application application) {
        this.application = application;
    }

    public boolean isDebuggerConnected() {

        return Debug.isDebuggerConnected();
    }

    public boolean isDebuggableBuild() {
        return (application.getApplicationInfo().flags
                & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}