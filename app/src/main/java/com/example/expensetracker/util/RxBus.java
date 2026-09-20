package com.example.expensetracker.util;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxBus {
    private static final RxBus instance = new RxBus();
    private final PublishSubject<Object> subject = PublishSubject.create();

    private RxBus() {}

    public static RxBus getInstance() {
        return instance;
    }

    public void publish(Object event) {
        subject.onNext(event);
    }

    public Observable<Object> toObservable() {
        return subject;
    }
}
