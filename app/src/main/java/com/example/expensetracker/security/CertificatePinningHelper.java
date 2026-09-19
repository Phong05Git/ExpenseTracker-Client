package com.example.expensetracker.security;

import okhttp3.CertificatePinner;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class CertificatePinningHelper {
    private static final String API_HOST = "expensetracker-api-b1xz.onrender.com";
    private static final String API_PIN = "sha256/fizfE9JVlzlRplEx7epXfqW9enrbLvwF/LU26XTPEG4=";

    @Inject
    public CertificatePinningHelper() {
    }

    public CertificatePinner createCertificatePinner() {
        return new CertificatePinner.Builder()
                .add(API_HOST, API_PIN)
                .build();
    }
}