package com.example.budgettracker.ui.transactions;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgettracker.R;
import com.example.budgettracker.data.AppDatabaseHelper;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Account;
import com.example.budgettracker.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionDialogHelper {

    public static final String[] CATEGORIES = {
            "Food",
            "Entertainment",
            "Travelling",
            "Education/College",
            "Money Lent",
            "Personal Expense",
            "Miscellaneous"
    };

    public static final String[] DEFAULT_ACCOUNTS = {
            "HDFC Bank",
            "Cash",
            "Credit Card"
    };

    public interface OnTransactionAddedListener {
        void onTransactionAdded(boolean isIncome);
    }

    public static void showAddTransactionDialog(Context context, OnTransactionAddedListener listener) {
        showAddTransactionDialog(context, false, listener);
    }

    public static void showAddTransactionDialog(Context context, boolean defaultIncome, OnTransactionAddedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_new_transaction);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_transaction, null);
        builder.setView(dialogView);

        RadioGroup rgTransactionType = dialogView.findViewById(R.id.rgTransactionType);
        RadioButton rbIncome = dialogView.findViewById(R.id.rbIncome);
        RadioButton rbExpenditure = dialogView.findViewById(R.id.rbExpenditure);

        TextView tvTitleLabel = dialogView.findViewById(R.id.tvTitleLabel);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);

        TextView tvCategoryLabel = dialogView.findViewById(R.id.tvCategoryLabel);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        Spinner spinnerAccount = dialogView.findViewById(R.id.spinnerAccount);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);

        if (defaultIncome) {
            rbIncome.setChecked(true);
            tvTitleLabel.setText(R.string.title_income_label);
            etTitle.setHint(R.string.hint_income_title);
            tvCategoryLabel.setVisibility(View.GONE);
            spinnerCategory.setVisibility(View.GONE);
        } else {
            rbExpenditure.setChecked(true);
            tvTitleLabel.setText(R.string.title_expenditure_label);
            etTitle.setHint(R.string.hint_expenditure_title);
            tvCategoryLabel.setVisibility(View.VISIBLE);
            spinnerCategory.setVisibility(View.VISIBLE);
        }

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        final String[] selectedDateHolder = { dateFormat.format(calendar.getTime()) };
        btnSelectDate.setText(selectedDateHolder[0]);

        btnSelectDate.setOnClickListener(v -> new DatePickerDialog(context, (view1, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDateHolder[0] = dateFormat.format(calendar.getTime());
            btnSelectDate.setText(selectedDateHolder[0]);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Load account list dynamically from Database
        List<Account> accountList = AppDatabaseHelper.getInstance(context).getAllAccounts();
        List<String> accountNames = new ArrayList<>();
        for (Account a : accountList) {
            accountNames.add(a.getName());
        }
        if (accountNames.isEmpty()) {
            for (String defAcc : DEFAULT_ACCOUNTS) {
                accountNames.add(defAcc);
            }
        }

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, accountNames
        );
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(accountAdapter);

        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIncome) {
                tvTitleLabel.setText(R.string.title_income_label);
                etTitle.setHint(R.string.hint_income_title);
                tvCategoryLabel.setVisibility(View.GONE);
                spinnerCategory.setVisibility(View.GONE);
            } else {
                tvTitleLabel.setText(R.string.title_expenditure_label);
                etTitle.setHint(R.string.hint_expenditure_title);
                tvCategoryLabel.setVisibility(View.VISIBLE);
                spinnerCategory.setVisibility(View.VISIBLE);
            }
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            boolean isIncome = rbIncome.isChecked();

            if (title.isEmpty()) {
                Toast.makeText(context, isIncome ? "Please enter source/whom & where" : "Please enter transaction title", Toast.LENGTH_SHORT).show();
            } else if (amountStr.isEmpty()) {
                Toast.makeText(context, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else {
                double amount = Double.parseDouble(amountStr);
                String category = isIncome ? "Income" : spinnerCategory.getSelectedItem().toString();
                String account = spinnerAccount.getSelectedItem() != null ? spinnerAccount.getSelectedItem().toString() : "HDFC Bank";

                Transaction t = new Transaction(title, category, account, amount, isIncome, selectedDateHolder[0]);
                TransactionManager.getInstance().addTransaction(t);

                Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onTransactionAdded(isIncome);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_rounded_dialog);
        }
        dialog.show();
    }
}
