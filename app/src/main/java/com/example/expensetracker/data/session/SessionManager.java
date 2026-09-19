package com.example.expensetracker.data.session;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

@Singleton
public final class SessionManager {
    private final Subject<Object> sessionExpiredSubject =
            PublishSubject.<Object>create().toSerialized();

    private boolean sessionExpired;
    private boolean sessionActive;
    private long sessionGeneration;

    @Inject
    public SessionManager() {
    }

    public Observable<Object> observeSessionExpired() {
        return sessionExpiredSubject.hide();
    }

    public synchronized void notifySessionExpired() {
        if (sessionExpired) {
            return;
        }

        sessionExpired = true;
        sessionActive = false;
        sessionGeneration++;
        sessionExpiredSubject.onNext(new Object());
    }

    public synchronized void invalidateSession() {
        sessionGeneration++;
        sessionExpired = true;
        sessionActive = false;
        sessionExpiredSubject.onNext(new Object());
    }

    public synchronized long getSessionGeneration() {
        return sessionGeneration;
    }

    public synchronized void reset() {
        sessionExpired = false;
        sessionActive = true;
        sessionGeneration++;
    }

    public synchronized boolean isSessionActive() {
        return sessionActive && !sessionExpired;
    }

    public synchronized boolean isSessionExpired() {
        return sessionExpired;
    }
}
