package com.example.budgettracker.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.EditAccountActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.data.BudgetManager;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.ui.transactions.TransactionDialogHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvBudgetStatus, tvTotalBalance, tvIncome, tvExpense, btnDashboardPeriod;
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
        btnDashboardPeriod = view.findViewById(R.id.btnDashboardPeriod);

        View accountCard1 = view.findViewById(R.id.accountCard1);
        View accountCard2 = view.findViewById(R.id.accountCard2);
        View accountCard3 = view.findViewById(R.id.accountCard3);
        View cardAddAccount = view.findViewById(R.id.cardAddAccount);
        MaterialCardView cardBudget = view.findViewById(R.id.cardBudget);

        if (accountCard1 != null) {
            tvBalance1 = accountCard1.findViewById(R.id.tvAccountBalance);
            TextView tvName1 = accountCard1.findViewById(R.id.tvAccountName);
            ImageView ivIcon1 = accountCard1.findViewById(R.id.ivAccountIcon);
            tvName1.setText(R.string.account_hdfc);
            if (ivIcon1 != null) ivIcon1.setImageResource(R.drawable.ic_bank);
            accountCard1.setOnClickListener(v -> openEditAccount("HDFC Bank", "Savings Account", tvBalance1.getText().toString()));
        }
        if (accountCard2 != null) {
            tvBalance2 = accountCard2.findViewById(R.id.tvAccountBalance);
            TextView tvName2 = accountCard2.findViewById(R.id.tvAccountName);
            TextView tvType2 = accountCard2.findViewById(R.id.tvAccountType);
            View iconBg2 = accountCard2.findViewById(R.id.viewAccountIconBg);
            ImageView ivIcon2 = accountCard2.findViewById(R.id.ivAccountIcon);

            if (accountCard2 instanceof MaterialCardView) {
                ((MaterialCardView) accountCard2).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pastel_green_bg));
            }
            if (iconBg2 != null) iconBg2.setBackgroundResource(R.drawable.bg_circle_green);
            if (ivIcon2 != null) {
                ivIcon2.setImageResource(R.drawable.ic_money);
                ivIcon2.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.pastel_green_icon)));
            }
            tvName2.setText(R.string.account_cash);
            if (tvType2 != null) tvType2.setText(R.string.account_type_wallet);
            accountCard2.setOnClickListener(v -> openEditAccount("Cash", "Cash / Wallet", tvBalance2.getText().toString()));
        }
        if (accountCard3 != null) {
            tvBalance3 = accountCard3.findViewById(R.id.tvAccountBalance);
            TextView tvName3 = accountCard3.findViewById(R.id.tvAccountName);
            TextView tvType3 = accountCard3.findViewById(R.id.tvAccountType);
            View iconBg3 = accountCard3.findViewById(R.id.viewAccountIconBg);
            ImageView ivIcon3 = accountCard3.findViewById(R.id.ivAccountIcon);

            if (accountCard3 instanceof MaterialCardView) {
                ((MaterialCardView) accountCard3).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pastel_purple_bg));
            }
            if (iconBg3 != null) iconBg3.setBackgroundResource(R.drawable.bg_circle_purple);
            if (ivIcon3 != null) {
                ivIcon3.setImageResource(R.drawable.ic_credit_card);
                ivIcon3.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.pastel_purple_icon)));
            }
            tvName3.setText(R.string.account_credit_card);
            if (tvType3 != null) tvType3.setText(R.string.account_type_credit_card);
            accountCard3.setOnClickListener(v -> openEditAccount("Credit Card", "Credit Card", tvBalance3.getText().toString()));
        }

        if (cardAddAccount != null) {
            cardAddAccount.setOnClickListener(v -> openEditAccount("", "", ""));
        }

        if (cardBudget != null) {
            cardBudget.setOnClickListener(v -> showEditBudgetDialog());
        }

        View btnAddIncome = view.findViewById(R.id.btnAddIncome);
        if (btnAddIncome != null) {
            btnAddIncome.setOnClickListener(v -> 
                TransactionDialogHelper.showAddTransactionDialog(requireContext(), true, isIncome -> updateDashboardData())
            );
        }

        View btnAddExpense = view.findViewById(R.id.btnAddExpense);
        if (btnAddExpense != null) {
            btnAddExpense.setOnClickListener(v -> 
                TransactionDialogHelper.showAddTransactionDialog(requireContext(), false, isIncome -> updateDashboardData())
            );
        }

        if (btnDashboardPeriod != null) {
            btnDashboardPeriod.setOnClickListener(v -> showPeriodSelectorDialog(btnDashboardPeriod));
        }

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
            tvTotalBalance.setText(String.format(Locale.getDefault(), "₹%,.2f", tm.getTotalBalance()));
        }
        if (tvIncome != null) {
            tvIncome.setText(String.format(Locale.getDefault(), "↑ +₹%,.2f", tm.getTotalIncome()));
        }
        if (tvExpense != null) {
            tvExpense.setText(String.format(Locale.getDefault(), "↓ -₹%,.2f", tm.getTotalExpense()));
        }

        if (tvBalance1 != null) {
            tvBalance1.setText(String.format(Locale.getDefault(), "₹%,.2f", tm.getAccountBalance("HDFC Bank")));
        }
        if (tvBalance2 != null) {
            tvBalance2.setText(String.format(Locale.getDefault(), "₹%,.2f", tm.getAccountBalance("Cash")));
        }
        if (tvBalance3 != null) {
            tvBalance3.setText(String.format(Locale.getDefault(), "₹%,.2f", tm.getAccountBalance("Credit Card")));
        }

        double spent = tm.getTotalExpense();
        double limit = bm.getOverallLimit();
        int progress = limit > 0 ? (int) ((spent / limit) * 100) : 0;
        if (tvBudgetStatus != null) {
            tvBudgetStatus.setText(String.format(Locale.getDefault(), "Spent ₹%,.0f of ₹%,.0f (%d%%)", spent, limit, progress));
        }
        if (budgetProgressBar != null) {
            budgetProgressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void showPeriodSelectorDialog(TextView targetTextView) {
        String[] periods = {"This Month", "Last Month", "This Year", "All Time"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Time Period")
                .setItems(periods, (dialog, which) -> {
                    targetTextView.setText(periods[which] + " ▾");
                    Toast.makeText(getContext(), "Showing data for " + periods[which], Toast.LENGTH_SHORT).show();
                    updateDashboardData();
                })
                .show();
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
        input.setText(String.format(Locale.getDefault(), "%d", (int) BudgetManager.getInstance().getOverallLimit()));
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
