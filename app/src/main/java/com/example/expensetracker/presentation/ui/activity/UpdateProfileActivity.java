package com.example.expensetracker.presentation.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.common.Resource;
import com.example.expensetracker.presentation.viewmodel.ProfileViewModel;
import com.example.expensetracker.util.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class UpdateProfileActivity extends AppCompatActivity {
    private ProfileViewModel profileViewModel;

    private TextInputEditText editFullName;
    private TextInputEditText editEmail;
    private TextInputLayout fullNameLayout;
    private TextInputLayout emailLayout;
    private MaterialButton buttonSave;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_update_profile);

        initializeViews();

        profileViewModel =
                new ViewModelProvider(this)
                        .get(ProfileViewModel.class);

        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(
                view -> finish());

        observeProfile();
        observeUpdate();

        profileViewModel.loadProfile();

        buttonSave.setOnClickListener(
                view -> updateProfile());

        ValidationUtils.bindLiveValidation(editFullName, fullNameLayout,
                () -> ValidationUtils.fullNameError(getText(editFullName)));
        ValidationUtils.bindLiveValidation(editEmail, emailLayout,
                () -> ValidationUtils.emailError(getText(editEmail)));
    }

    private void initializeViews() {
        editFullName =
                findViewById(R.id.editFullName);

        editEmail =
                findViewById(R.id.editEmail);

        fullNameLayout = findViewById(R.id.fullNameLayout);
        emailLayout = findViewById(R.id.emailLayout);

        buttonSave =
                findViewById(R.id.buttonSave);
    }

    private void observeProfile() {
        profileViewModel
                .getProfileState()
                .observe(
                        this,
                        resource -> {
                            if (resource == null ||
                                    resource.getData() == null) {
                                return;
                            }

                            editFullName.setText(
                                    resource.getData()
                                            .getFullName());

                            editEmail.setText(
                                    resource.getData()
                                            .getEmail());
                        });
    }

    private void observeUpdate() {
        profileViewModel
                .getUpdateState()
                .observe(
                        this,
                        resource -> {
                            if (resource == null) {
                                return;
                            }

                            switch (resource.getStatus()) {
                                case LOADING:
                                    buttonSave.setEnabled(false);
                                    break;

                                case SUCCESS:
                                    buttonSave.setEnabled(true);

                                    Toast.makeText(
                                                    this,
                                                    "Cập nhật thông tin thành công.",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                    finish();
                                    break;

                                case ERROR:
                                    buttonSave.setEnabled(true);

                                    Toast.makeText(
                                                    this,
                                                    resource.getMessage(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    break;

                                case IDLE:
                                default:
                                    buttonSave.setEnabled(true);
                                    break;
                            }
                        });
    }

    private void updateProfile() {
        String fullName =
                getText(editFullName);

        String email =
                getText(editEmail);

        boolean valid = ValidationUtils.setError(fullNameLayout, ValidationUtils.fullNameError(fullName));
        valid &= ValidationUtils.setError(emailLayout, ValidationUtils.emailError(email));
        if (!valid) return;

        buttonSave.setEnabled(false);

        profileViewModel.updateProfile(
                fullName,
                email);
    }

    private String getText(
            TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString()
                .trim();
    }
}
