package com.example.budgettracker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ACCOUNT_TYPES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        String name = getIntent().getStringExtra("account_name");
        String type = getIntent().getStringExtra("account_type");
        String balance = getIntent().getStringExtra("account_balance");

        if (name != null) etAccountName.setText(name);
        if (balance != null) etAccountBalance.setText(balance);

        if (type != null) {
            int selectedIndex = findAccountTypeIndex(type);
            if (selectedIndex >= 0) {
                spinnerAccountType.setSelection(selectedIndex);
            }
        }

        btnSaveAccount.setOnClickListener(v -> {
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
