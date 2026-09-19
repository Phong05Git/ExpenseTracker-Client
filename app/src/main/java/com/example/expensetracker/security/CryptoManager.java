package com.example.expensetracker.security;

import android.content.Context;
import android.util.Base64;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class CryptoManager {
    private static final String KEYSET_NAME = "expense_tracker_token_keyset";
    private static final String PREF_FILE_NAME = "expense_tracker_crypto";
    private static final String MASTER_KEY_URI = "android-keystore://expense_tracker_master_key";
    private final Aead aead;

    @Inject
    public CryptoManager(@ApplicationContext Context context) {
        try {
            AeadConfig.register();

            AndroidKeysetManager keysetManager = new AndroidKeysetManager.Builder()
                    .withSharedPref(context.getApplicationContext(), KEYSET_NAME, PREF_FILE_NAME)
                    .withMasterKeyUri(MASTER_KEY_URI)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .build();

            aead = keysetManager.getKeysetHandle().getPrimitive(Aead.class);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Unable to initialize token encryption.", e);
        }
    }

    public String encrypt(String plaintext) {
        try {
            byte[] ciphertext = aead.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), new byte[0]);
            return Base64.encodeToString(ciphertext, Base64.NO_WRAP);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to encrypt token.", e);
        }
    }

    public String decrypt(String ciphertext) {
        try {
            byte[] decoded = Base64.decode(ciphertext, Base64.NO_WRAP);
            byte[] plaintext = aead.decrypt(decoded, new byte[0]);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to decrypt token.", e);
        }
    }
}