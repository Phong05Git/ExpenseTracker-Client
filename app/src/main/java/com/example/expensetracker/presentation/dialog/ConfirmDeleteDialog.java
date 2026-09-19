package com.example.expensetracker.presentation.dialog;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

public final class ConfirmDeleteDialog {

    public interface Listener {
        void onConfirm();
    }

    private ConfirmDeleteDialog() {
    }

    public static void show(
            @NonNull Context context,
            @NonNull String title,
            @NonNull String message,
            @NonNull Listener listener) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(
                        "Hủy",
                        null)
                .setPositiveButton(
                        "Xóa",
                        (dialog, which) ->
                                listener.onConfirm())
                .show();
    }
}