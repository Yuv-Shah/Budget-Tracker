package com.example.budgettracker.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.EditAccountActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.data.BudgetManager;
import com.example.budgettracker.data.TransactionManager;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    private TextView tvBudgetStatus, tvTotalBalance, tvIncome, tvExpense;
    private ProgressBar budgetProgressBar;
    private TextView tvBalance1, tvBalance2, tvBalance3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvBudgetStatus = view.findViewById(R.id.tvBudgetStatus);
        budgetProgressBar = view.findViewById(R.id.budgetProgressBar);

        View accountCard1 = view.findViewById(R.id.accountCard1);
        View accountCard2 = view.findViewById(R.id.accountCard2);
        View accountCard3 = view.findViewById(R.id.accountCard3);
        View cardAddAccount = view.findViewById(R.id.cardAddAccount);
        MaterialCardView cardBudget = view.findViewById(R.id.cardBudget);

        if (accountCard1 != null) {
            tvBalance1 = accountCard1.findViewById(R.id.tvAccountBalance);
            TextView tvName1 = accountCard1.findViewById(R.id.tvAccountName);
            tvName1.setText("HDFC Bank");
            accountCard1.setOnClickListener(v -> openEditAccount("HDFC Bank", "Savings", tvBalance1.getText().toString()));
        }
        if (accountCard2 != null) {
            tvBalance2 = accountCard2.findViewById(R.id.tvAccountBalance);
            TextView tvName2 = accountCard2.findViewById(R.id.tvAccountName);
            tvName2.setText("Cash");
            accountCard2.setOnClickListener(v -> openEditAccount("Cash", "Wallet", tvBalance2.getText().toString()));
        }
        if (accountCard3 != null) {
            tvBalance3 = accountCard3.findViewById(R.id.tvAccountBalance);
            TextView tvName3 = accountCard3.findViewById(R.id.tvAccountName);
            tvName3.setText("Credit Card");
            accountCard3.setOnClickListener(v -> openEditAccount("Credit Card", "Credit", tvBalance3.getText().toString()));
        }

        if (cardAddAccount != null) {
            cardAddAccount.setOnClickListener(v -> openEditAccount("", "", ""));
        }

        if (cardBudget != null) {
            cardBudget.setOnClickListener(v -> showEditBudgetDialog());
        }

        Button btnAddTransaction = view.findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Use the Transactions tab to add new transactions", Toast.LENGTH_SHORT).show()
        );

        updateDashboardData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboardData();
    }

    private void updateDashboardData() {
        TransactionManager tm = TransactionManager.getInstance();
        BudgetManager bm = BudgetManager.getInstance();

        if (tvTotalBalance != null) {
            tvTotalBalance.setText("₹" + String.format("%,.2f", tm.getTotalBalance()));
        }
        if (tvIncome != null) {
            tvIncome.setText("+₹" + String.format("%,.2f", tm.getTotalIncome()));
        }
        if (tvExpense != null) {
            tvExpense.setText("-₹" + String.format("%,.2f", tm.getTotalExpense()));
        }

        if (tvBalance1 != null) {
            tvBalance1.setText("₹" + String.format("%,.2f", tm.getAccountBalance("HDFC Bank")));
        }
        if (tvBalance2 != null) {
            tvBalance2.setText("₹" + String.format("%,.2f", tm.getAccountBalance("Cash")));
        }
        if (tvBalance3 != null) {
            tvBalance3.setText("₹" + String.format("%,.2f", tm.getAccountBalance("Credit Card")));
        }

        double spent = tm.getTotalExpense();
        double limit = bm.getOverallLimit();
        int progress = limit > 0 ? (int) ((spent / limit) * 100) : 0;
        if (tvBudgetStatus != null) {
            tvBudgetStatus.setText(String.format("Spent ₹%,.0f of ₹%,.0f (%d%%)", spent, limit, progress));
        }
        if (budgetProgressBar != null) {
            budgetProgressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void openEditAccount(String name, String type, String balance) {
        Intent intent = new Intent(getContext(), EditAccountActivity.class);
        intent.putExtra("account_name", name);
        intent.putExtra("account_type", type);
        intent.putExtra("account_balance", balance);
        startActivity(intent);
    }

    private void showEditBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.edit_budget);

        final EditText input = new EditText(requireContext());
        input.setHint(R.string.enter_budget_limit);
        input.setText(String.valueOf((int) BudgetManager.getInstance().getOverallLimit()));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = input.getText().toString();
            if (!val.isEmpty()) {
                double newLimit = Double.parseDouble(val);
                BudgetManager.getInstance().setOverallLimit(newLimit);
                updateDashboardData();
                Toast.makeText(getContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
