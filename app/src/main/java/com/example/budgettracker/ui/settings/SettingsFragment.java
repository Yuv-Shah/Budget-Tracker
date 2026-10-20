package com.example.budgettracker.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.ProfileActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.data.AppDatabaseHelper;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Executor;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;
    private ActivityResultLauncher<String> csvExportLauncher;
    private ActivityResultLauncher<String[]> csvImportLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = requireActivity().getSharedPreferences("BudgetPrefs", Activity.MODE_PRIVATE);

        MaterialCardView cardProfile = view.findViewById(R.id.cardProfile);
        Spinner spinnerTheme = view.findViewById(R.id.spinnerTheme);
        SwitchMaterial switchAppLock = view.findViewById(R.id.switchAppLock);
        MaterialButton btnExportCsv = view.findViewById(R.id.btnExportCsv);
        MaterialButton btnImportCsv = view.findViewById(R.id.btnImportCsv);

        if (cardProfile != null) {
            cardProfile.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), ProfileActivity.class));
            });
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.theme_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adapter);

        int currentThemeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_NO) {
            spinnerTheme.setSelection(0);
        } else if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES) {
            spinnerTheme.setSelection(1);
        } else {
            spinnerTheme.setSelection(2);
        }

        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                int mode;
                if (position == 0) {
                    mode = AppCompatDelegate.MODE_NIGHT_NO;
                } else if (position == 1) {
                    mode = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                }
                if (prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) != mode) {
                    prefs.edit().putInt("theme_mode", mode).apply();
                    AppCompatDelegate.setDefaultNightMode(mode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        boolean isLockEnabled = prefs.getBoolean("app_lock_enabled", false);
        switchAppLock.setChecked(isLockEnabled);

        switchAppLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                promptBiometricForSetup(switchAppLock);
            } else {
                prefs.edit().putBoolean("app_lock_enabled", false).apply();
                Toast.makeText(getContext(), "App Lock disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Register Storage Access Framework (SAF) Launchers for CSV Export & Import
        csvExportLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/csv"),
                uri -> {
                    if (uri != null) {
                        writeCsvToUri(uri);
                    }
                }
        );

        csvImportLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        importCsvData(uri);
                    }
                }
        );

        btnExportCsv.setOnClickListener(v -> {
            String defaultFileName = "budget_tracker_export_" + System.currentTimeMillis() + ".csv";
            csvExportLauncher.launch(defaultFileName);
        });

        btnImportCsv.setOnClickListener(v -> {
            csvImportLauncher.launch(new String[]{
                    "text/csv",
                    "text/comma-separated-values",
                    "application/csv",
                    "text/plain",
                    "*/*"
            });
        });

        MaterialButton btnClearAllData = view.findViewById(R.id.btnClearAllData);
        if (btnClearAllData != null) {
            btnClearAllData.setOnClickListener(v -> showClearDataConfirmationDialog());
        }

        return view;
    }

    private void showClearDataConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All App Data")
                .setMessage("Are you sure you want to delete all transactions, accounts, budgets, and user settings? This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    AppDatabaseHelper.getInstance(requireContext()).clearAllData();
                    prefs.edit().clear().apply();
                    Toast.makeText(getContext(), "All app data cleared successfully!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptBiometricForSetup(SwitchMaterial switchAppLock) {
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switchAppLock.setChecked(false);
                Toast.makeText(getContext(), "Authentication cancelled or failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                prefs.edit().putBoolean("app_lock_enabled", true).apply();
                Toast.makeText(getContext(), "App Lock enabled successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enable App Lock")
                .setSubtitle("Confirm your biometric or device credential to enable App Lock")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        try {
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e) {
            switchAppLock.setChecked(false);
            Toast.makeText(getContext(), "Biometric / Device credential not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeCsvToUri(Uri uri) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("type,amount,title,category,account,date\n");
            for (Transaction t : TransactionManager.getInstance().getTransactions()) {
                sb.append(t.isIncome() ? "INCOME" : "EXPENSE").append(",")
                  .append(t.getAmount()).append(",")
                  .append("\"").append(t.getTitle()).append("\",")
                  .append("\"").append(t.getCategory()).append("\",")
                  .append("\"").append(t.getAccountName()).append("\",")
                  .append("\"").append(t.getDate()).append("\"\n");
            }

            OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(sb.toString().getBytes());
                outputStream.close();
                Toast.makeText(getContext(), "CSV exported successfully to chosen location!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Failed to open export file destination.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importCsvData(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(getContext(), "Unable to read imported file.", Toast.LENGTH_SHORT).show();
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String fileLine;
            int newlyAdded = 0;
            int existingUpdated = 0;
            boolean isFirstLine = true;
            while ((fileLine = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = fileLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 6) {
                    boolean isIncome = parts[0].equalsIgnoreCase("INCOME");
                    double amount = Double.parseDouble(parts[1]);
                    String title = parts[2].replace("\"", "");
                    String category = parts[3].replace("\"", "");
                    String account = parts[4].replace("\"", "");
                    String date = parts[5].replace("\"", "");

                    Transaction t = new Transaction(title, category, account, amount, isIncome, date);
                    boolean isNew = TransactionManager.getInstance().addOrUpdateTransactionFromCsv(t);
                    if (isNew) {
                        newlyAdded++;
                    } else {
                        existingUpdated++;
                    }
                }
            }
            reader.close();
            Toast.makeText(getContext(), "Import complete: " + newlyAdded + " new added, " + existingUpdated + " existing updated/overwritten!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Import failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
