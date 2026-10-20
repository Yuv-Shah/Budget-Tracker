package com.example.budgettracker.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.EditAccountActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.data.AppDatabaseHelper;
import com.example.budgettracker.data.BudgetManager;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Account;
import com.example.budgettracker.ui.transactions.TransactionDialogHelper;
import com.example.budgettracker.utils.CurrencyUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvHeaderGreetingName, tvBudgetStatus, tvTotalBalance, tvIncome, tvExpense, btnDashboardPeriod;
    private ProgressBar budgetProgressBar;
    private LinearLayout layoutAccountsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvHeaderGreetingName = view.findViewById(R.id.tvHeaderGreetingName);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvBudgetStatus = view.findViewById(R.id.tvBudgetStatus);
        budgetProgressBar = view.findViewById(R.id.budgetProgressBar);
        btnDashboardPeriod = view.findViewById(R.id.btnDashboardPeriod);
        layoutAccountsContainer = view.findViewById(R.id.layoutAccountsContainer);

        MaterialCardView cardBudget = view.findViewById(R.id.cardBudget);

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

        if (getContext() != null) {
            android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("BudgetPrefs", android.content.Context.MODE_PRIVATE);
            String savedName = prefs.getString("user_name", "Nishant");
            if (tvHeaderGreetingName != null) {
                tvHeaderGreetingName.setText(savedName + " 👋");
            }
        }

        if (tvTotalBalance != null) {
            tvTotalBalance.setText(CurrencyUtils.formatRupee(tm.getTotalBalance()));
        }
        if (tvIncome != null) {
            tvIncome.setText("↑ +" + CurrencyUtils.formatRupee(tm.getTotalIncome()));
        }
        if (tvExpense != null) {
            tvExpense.setText("↓ -" + CurrencyUtils.formatRupee(tm.getTotalExpense()));
        }

        // Render Accounts Dynamically from Database
        renderAccountsDynamically();

        double spent = tm.getTotalExpense();
        double limit = bm.getOverallLimit();
        int progress = limit > 0 ? (int) ((spent / limit) * 100) : 0;
        if (tvBudgetStatus != null) {
            tvBudgetStatus.setText(String.format(Locale.getDefault(), "Spent %s of %s (%d%%)",
                    CurrencyUtils.formatRupeeNoDecimals(spent), CurrencyUtils.formatRupeeNoDecimals(limit), progress));
        }
        if (budgetProgressBar != null) {
            budgetProgressBar.setProgress(Math.min(progress, 100));
        }
    }

    private void renderAccountsDynamically() {
        if (layoutAccountsContainer == null || getContext() == null) return;
        layoutAccountsContainer.removeAllViews();

        List<Account> accounts = AppDatabaseHelper.getInstance(requireContext()).getAllAccounts();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        int[] bgColors = {
                R.color.pastel_blue_bg,
                R.color.pastel_green_bg,
                R.color.pastel_purple_bg,
                R.color.pastel_orange_bg
        };
        int[] circleBgs = {
                R.drawable.bg_circle_blue,
                R.drawable.bg_circle_green,
                R.drawable.bg_circle_purple,
                R.drawable.bg_circle_orange
        };
        int[] iconColors = {
                R.color.pastel_blue_icon,
                R.color.pastel_green_icon,
                R.color.pastel_purple_icon,
                R.color.pastel_orange_icon
        };
        int[] iconDrawables = {
                R.drawable.ic_bank,
                R.drawable.ic_money,
                R.drawable.ic_credit_card,
                R.drawable.ic_money
        };

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            View cardView = inflater.inflate(R.layout.item_account_card, layoutAccountsContainer, false);

            int colorIdx = i % bgColors.length;

            if (cardView instanceof MaterialCardView) {
                ((MaterialCardView) cardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), bgColors[colorIdx]));
            }

            TextView tvName = cardView.findViewById(R.id.tvAccountName);
            TextView tvType = cardView.findViewById(R.id.tvAccountType);
            TextView tvBalance = cardView.findViewById(R.id.tvAccountBalance);
            FrameLayout viewIconBg = cardView.findViewById(R.id.viewAccountIconBg);
            ImageView ivIcon = cardView.findViewById(R.id.ivAccountIcon);

            if (tvName != null) tvName.setText(account.getName());
            if (tvType != null) tvType.setText(account.getType());

            double balance = TransactionManager.getInstance().getAccountBalance(account.getName());
            if (tvBalance != null) {
                tvBalance.setText(CurrencyUtils.formatRupee(balance));
            }

            if (viewIconBg != null) {
                viewIconBg.setBackgroundResource(circleBgs[colorIdx]);
            }
            if (ivIcon != null) {
                ivIcon.setImageResource(iconDrawables[colorIdx]);
                ivIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), iconColors[colorIdx])));
            }

            cardView.setOnClickListener(v -> openEditAccount(account.getName(), account.getType(), String.format(Locale.getDefault(), "%.2f", balance)));
            layoutAccountsContainer.addView(cardView);
        }

        // Add "Add Account" Card
        View addCardView = inflater.inflate(R.layout.item_add_account_card, layoutAccountsContainer, false);
        addCardView.setOnClickListener(v -> openEditAccount("", "", ""));
        layoutAccountsContainer.addView(addCardView);
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
