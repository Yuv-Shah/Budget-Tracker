package com.example.budgettracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettracker.data.AppDatabaseHelper;
import com.example.budgettracker.data.TransactionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        ImageButton btnBackProfile = findViewById(R.id.btnBackProfile);
        if (btnBackProfile != null) {
            btnBackProfile.setOnClickListener(v -> finish());
        }

        TextInputEditText etProfileName = findViewById(R.id.etProfileName);
        TextInputEditText etProfileEmail = findViewById(R.id.etProfileEmail);
        MaterialButton btnSaveProfile = findViewById(R.id.btnSaveProfile);

        String savedName = prefs.getString("user_name", "Nishant");
        String savedEmail = prefs.getString("user_email", "nishant@example.com");

        if (etProfileName != null) etProfileName.setText(savedName);
        if (etProfileEmail != null) etProfileEmail.setText(savedEmail);

        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> {
                String newName = etProfileName != null && etProfileName.getText() != null ?
                        etProfileName.getText().toString().trim() : "";
                String newEmail = etProfileEmail != null && etProfileEmail.getText() != null ?
                        etProfileEmail.getText().toString().trim() : "";

                if (newName.isEmpty()) {
                    Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                prefs.edit()
                        .putString("user_name", newName)
                        .putString("user_email", newEmail)
                        .apply();

                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            });
        }

        TextView tvProfileStats = findViewById(R.id.tvProfileStats);
        if (tvProfileStats != null) {
            AppDatabaseHelper db = AppDatabaseHelper.getInstance(this);
            int transCount = db.getAllTransactions().size();
            int accCount = db.getAllAccounts().size();
            double totalBalance = TransactionManager.getInstance().getTotalBalance();

            String stats = String.format(Locale.getDefault(),
                    "Total Transactions: %d\nActive Accounts: %d\nTotal Net Balance: ₹%,.2f",
                    transCount, accCount, totalBalance);
            tvProfileStats.setText(stats);
        }
    }
}
