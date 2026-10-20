package com.example.budgettracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettracker.data.AppDatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class EditAccountActivity extends AppCompatActivity {

    public static final String[] ACCOUNT_TYPES = {
            "Savings Account",
            "Current / Checking Account",
            "Credit Card",
            "Cash / Wallet",
            "Digital Wallet / UPI",
            "Investment / Stocks",
            "Fixed Deposit (FD)",
            "Loan Account"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        TextInputEditText etAccountName = findViewById(R.id.etAccountName);
        Spinner spinnerAccountType = findViewById(R.id.spinnerAccountType);
        TextInputEditText etAccountBalance = findViewById(R.id.etAccountBalance);
        Button btnSaveAccount = findViewById(R.id.btnSaveAccount);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ACCOUNT_TYPES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        String name = getIntent().getStringExtra("account_name");
        String type = getIntent().getStringExtra("account_type");
        String balance = getIntent().getStringExtra("account_balance");

        if (name != null) etAccountName.setText(name);
        if (balance != null) {
            String cleanBal = balance.replace("₹", "").replace(",", "").trim();
            etAccountBalance.setText(cleanBal);
        }

        if (type != null) {
            int selectedIndex = findAccountTypeIndex(type);
            if (selectedIndex >= 0) {
                spinnerAccountType.setSelection(selectedIndex);
            }
        }

        if (name != null && !name.trim().isEmpty()) {
            if (btnDeleteAccount != null) {
                btnDeleteAccount.setVisibility(View.VISIBLE);
                btnDeleteAccount.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Account")
                            .setMessage("Are you sure you want to delete the account '" + name + "'?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                AppDatabaseHelper.getInstance(this).deleteAccountByName(name);
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
            }
        }

        btnSaveAccount.setOnClickListener(v -> {
            String accName = etAccountName.getText() != null ? etAccountName.getText().toString().trim() : "";
            String accType = spinnerAccountType.getSelectedItem() != null ? spinnerAccountType.getSelectedItem().toString() : ACCOUNT_TYPES[0];
            String balStr = etAccountBalance.getText() != null ? etAccountBalance.getText().toString().trim() : "0";

            if (accName.isEmpty()) {
                Toast.makeText(this, "Please enter an account name", Toast.LENGTH_SHORT).show();
                return;
            }

            double initialBal = 0;
            try {
                if (!balStr.isEmpty()) {
                    initialBal = Double.parseDouble(balStr);
                }
            } catch (NumberFormatException e) {
                initialBal = 0;
            }

            AppDatabaseHelper.getInstance(this).insertOrUpdateAccount(accName, accType, initialBal);
            Toast.makeText(this, "Account saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private int findAccountTypeIndex(String type) {
        List<String> list = Arrays.asList(ACCOUNT_TYPES);
        return list.stream()
                .filter(t -> t.equalsIgnoreCase(type) || t.toLowerCase().contains(type.toLowerCase()))
                .mapToInt(list::indexOf)
                .findFirst()
                .orElse(-1);
    }
}
