package com.example.budgettracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class EditAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        TextInputEditText etAccountName = findViewById(R.id.etAccountName);
        TextInputEditText etAccountType = findViewById(R.id.etAccountType);
        TextInputEditText etAccountBalance = findViewById(R.id.etAccountBalance);
        Button btnSaveAccount = findViewById(R.id.btnSaveAccount);

        String name = getIntent().getStringExtra("account_name");
        String type = getIntent().getStringExtra("account_type");
        String balance = getIntent().getStringExtra("account_balance");

        if (name != null) etAccountName.setText(name);
        if (type != null) etAccountType.setText(type);
        if (balance != null) etAccountBalance.setText(balance);

        btnSaveAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Account saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
