package com.example.expensetracker.security;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CryptoManagerTest {

    @Test
    public void encryptAndDecrypt_shouldReturnOriginalText() {
        Context context =
                ApplicationProvider.getApplicationContext();

        CryptoManager cryptoManager =
                new CryptoManager(context);

        String originalText =
                "ExpenseTracker-Token-Test";

        String ciphertext =
                cryptoManager.encrypt(originalText);

        Assert.assertNotNull(ciphertext);
        Assert.assertNotEquals(
                originalText,
                ciphertext);

        String decryptedText =
                cryptoManager.decrypt(ciphertext);

        Assert.assertEquals(
                originalText,
                decryptedText);
    }

    @Test
    public void encryptionKey_shouldRemainUsableAcrossInstances() {
        Context context =
                ApplicationProvider.getApplicationContext();

        CryptoManager firstManager =
                new CryptoManager(context);

        String originalText =
                "Persistent-Key-Test";

        String ciphertext =
                firstManager.encrypt(originalText);

        CryptoManager secondManager =
                new CryptoManager(context);

        String decryptedText =
                secondManager.decrypt(ciphertext);

        Assert.assertEquals(
                originalText,
                decryptedText);
    }
}