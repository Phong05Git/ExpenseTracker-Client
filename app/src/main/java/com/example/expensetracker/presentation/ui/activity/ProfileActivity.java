package com.example.expensetracker.presentation.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.session.SessionManager;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.domain.model.User;
import com.example.expensetracker.domain.usecase.auth.LogoutUseCase;
import com.example.expensetracker.presentation.viewmodel.ProfileViewModel;
import com.example.expensetracker.service.ExpenseNotificationListenerService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public final class ProfileActivity extends AppCompatActivity {
    @Inject
    LogoutUseCase logoutUseCase;

    @Inject
    SessionManager sessionManager;

    private final CompositeDisposable disposables =
            new CompositeDisposable();

    private final CompositeDisposable sessionDisposables =
            new CompositeDisposable();

    private ProfileViewModel profileViewModel;

    private TextView textAvatar;
    private TextView textUsername;
    private TextView textFullName;
    private TextView textEmail;
    private TextView textNotificationStatus;

    private MaterialCardView itemUpdateProfile;
    private MaterialCardView itemChangePassword;
    private MaterialCardView itemLogout;

    private SwitchMaterial switchNotificationAccess;

    private boolean updatingNotificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        initializeViews();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupToolbar();
        setupListeners();
        observeProfile();
        observeSessionExpired();
    }

    @Override
    protected void onResume() {
        super.onResume();

        profileViewModel.loadProfile();
        updateNotificationAccessState();
    }

    private void initializeViews() {
        textAvatar = findViewById(R.id.textAvatar);
        textUsername = findViewById(R.id.textUsername);
        textFullName = findViewById(R.id.textFullName);
        textEmail = findViewById(R.id.textEmail);
        textNotificationStatus = findViewById(R.id.textNotificationStatus);

        itemUpdateProfile = findViewById(R.id.itemUpdateProfile);
        itemChangePassword = findViewById(R.id.itemChangePassword);
        itemLogout = findViewById(R.id.itemLogout);

        switchNotificationAccess = findViewById(R.id.switchNotificationAccess);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void setupListeners() {
        itemUpdateProfile.setOnClickListener(
                view -> startActivity(
                        new Intent(
                                this,
                                UpdateProfileActivity.class)));

        itemChangePassword.setOnClickListener(
                view -> startActivity(
                        new Intent(
                                this,
                                ChangePasswordActivity.class)));

        itemLogout.setOnClickListener(
                view -> confirmLogout());

        switchNotificationAccess.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (updatingNotificationSwitch) {
                        return;
                    }

                    if (isChecked) {
                        enableNotificationAccess();
                    } else {
                        disableNotificationAccess();
                    }
                });
    }

    private void observeProfile() {
        profileViewModel.getProfileState().observe(this, resource -> {
            if (resource == null) {
                return;
            }

            if (resource.getStatus() == Resource.Status.SUCCESS) {
                displayUser(resource.getData());
                return;
            }

            if (resource.getStatus() == Resource.Status.ERROR &&
                    !sessionManager.isSessionExpired()) {
                String message = resource.getMessage();

                if (message == null || message.isBlank()) {
                    message = "Không thể tải thông tin tài khoản.";
                }

                Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void observeSessionExpired() {
        sessionDisposables.add(
                sessionManager
                        .observeSessionExpired()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(value -> openLoginActivity()));
    }

    private void displayUser(User user) {
        if (user == null) {
            return;
        }

        String username = user.getUsername();

        textUsername.setText(username);
        textFullName.setText(user.getFullName());
        textEmail.setText(user.getEmail());

        if (username != null && !username.isBlank()) {
            textAvatar.setText(
                    username.substring(0, 1).toUpperCase());
        } else {
            textAvatar.setText("?");
        }
    }

    private void updateNotificationAccessState() {
        boolean enabled = isNotificationListenerEnabled();

        updatingNotificationSwitch = true;
        switchNotificationAccess.setChecked(enabled);
        updatingNotificationSwitch = false;

        textNotificationStatus.setText(
                enabled ? "Đang bật" : "Chưa bật");
    }

    private boolean isNotificationListenerEnabled() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(),
                "enabled_notification_listeners");

        if (enabledListeners == null || enabledListeners.isBlank()) {
            return false;
        }

        ComponentName componentName = new ComponentName(
                this,
                ExpenseNotificationListenerService.class);

        String expectedComponentName =
                componentName.flattenToString();

        for (String enabledListener : enabledListeners.split(":")) {
            ComponentName enabledComponent =
                    ComponentName.unflattenFromString(enabledListener);

            if (enabledComponent != null &&
                    expectedComponentName.equals(
                            enabledComponent.flattenToString())) {
                return true;
            }
        }

        return false;
    }

    private void enableNotificationAccess() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Bật tự động ghi nhận giao dịch?")
                .setMessage(
                        "ExpenseTracker sẽ đọc các thông báo biến động số dư từ MB Bank, Timo và BIDV để tự động tạo giao dịch trong ứng dụng. Các thông báo từ ứng dụng khác sẽ được bỏ qua. Bạn sẽ được chuyển đến phần cài đặt của Android để cấp quyền.")
                .setNegativeButton(
                        "Hủy",
                        (dialog, which) -> restoreNotificationSwitch())
                .setPositiveButton(
                        "Tiếp tục",
                        (dialog, which) -> openNotificationAccessSettings())
                .show();
    }

    private void disableNotificationAccess() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tắt tự động ghi nhận giao dịch?")
                .setMessage(
                        "ExpenseTracker sẽ không còn tự động đọc thông báo biến động số dư. Bạn sẽ được chuyển đến phần cài đặt của Android để tắt quyền Notification Access.")
                .setNegativeButton(
                        "Hủy",
                        (dialog, which) -> restoreNotificationSwitch())
                .setPositiveButton(
                        "Tiếp tục",
                        (dialog, which) -> openNotificationAccessSettings())
                .show();
    }

    private void restoreNotificationSwitch() {
        updateNotificationAccessState();
    }

    private void openNotificationAccessSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(
                            this,
                            "Không thể mở cài đặt Notification Access.",
                            Toast.LENGTH_LONG)
                    .show();

            updateNotificationAccessState();
        }
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Đăng xuất",
                        (dialog, which) -> logout())
                .show();
    }

    private void logout() {
        itemLogout.setEnabled(false);

        disposables.add(
                logoutUseCase
                        .execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onLogoutSuccess,
                                this::onLogoutError));
    }

    private void onLogoutSuccess() {
        itemLogout.setEnabled(true);
        sessionManager.reset();
        openLoginActivity();
    }

    private void onLogoutError(Throwable throwable) {
        itemLogout.setEnabled(true);
        sessionManager.reset();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        sessionDisposables.clear();
        super.onDestroy();
    }
}