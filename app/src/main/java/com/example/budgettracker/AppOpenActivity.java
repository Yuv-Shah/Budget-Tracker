package com.example.budgettracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class AppOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_open);

        Button btnEnter = findViewById(R.id.btnEnter);
        if (btnEnter != null) {
            btnEnter.setOnClickListener(v -> enterMain());
        }

        SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        boolean appLockEnabled = prefs.getBoolean("app_lock_enabled", false);

        if (!appLockEnabled) {
            enterMain();
            return;
        }

        authenticateAndEnter();
    }

    private void enterMain() {
        startActivity(new Intent(AppOpenActivity.this, MainActivity.class));
        finish();
    }

    private void authenticateAndEnter() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(AppOpenActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                enterMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Budget Tracker Security")
                .setSubtitle("Authenticate using your biometric credential or device password")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        try {
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e) {
            enterMain();
        }
    }
}
