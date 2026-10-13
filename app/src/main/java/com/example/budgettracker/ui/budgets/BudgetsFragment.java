package com.example.budgettracker.ui.budgets;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.R;
import com.example.budgettracker.data.BudgetManager;
import com.example.budgettracker.data.TransactionManager;

public class BudgetsFragment extends Fragment {

    private TextView tvOverallBudget, tvFoodBudget, tvTransportBudget, tvUtilitiesBudget;
    private ProgressBar progressBarOverall, progressBarFood, progressBarTransport, progressBarUtilities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budgets, container, false);

        tvOverallBudget = view.findViewById(R.id.tvOverallBudget);
        tvFoodBudget = view.findViewById(R.id.tvFoodBudget);
        tvTransportBudget = view.findViewById(R.id.tvTransportBudget);
        tvUtilitiesBudget = view.findViewById(R.id.tvUtilitiesBudget);

        progressBarOverall = view.findViewById(R.id.progressBarOverall);
        progressBarFood = view.findViewById(R.id.progressBarFood);
        progressBarTransport = view.findViewById(R.id.progressBarTransport);
        progressBarUtilities = view.findViewById(R.id.progressBarUtilities);

        View cardOverallBudget = view.findViewById(R.id.cardOverallBudget);
        View cardFoodBudget = view.findViewById(R.id.cardFoodBudget);
        View cardTransportBudget = view.findViewById(R.id.cardTransportBudget);
        View cardUtilitiesBudget = view.findViewById(R.id.cardUtilitiesBudget);

        if (cardOverallBudget != null) {
            cardOverallBudget.setOnClickListener(v -> showEditBudgetDialog("Overall", BudgetManager.getInstance().getOverallLimit()));
        }
        if (cardFoodBudget != null) {
            cardFoodBudget.setOnClickListener(v -> showEditBudgetDialog("Food", BudgetManager.getInstance().getBudgetLimit("Food")));
        }
        if (cardTransportBudget != null) {
            cardTransportBudget.setOnClickListener(v -> showEditBudgetDialog("Transport", BudgetManager.getInstance().getBudgetLimit("Transport")));
        }
        if (cardUtilitiesBudget != null) {
            cardUtilitiesBudget.setOnClickListener(v -> showEditBudgetDialog("Utilities", BudgetManager.getInstance().getBudgetLimit("Utilities")));
        }

        updateBudgetData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBudgetData();
    }

    private void updateBudgetData() {
        TransactionManager tm = TransactionManager.getInstance();
        BudgetManager bm = BudgetManager.getInstance();

        // Overall
        double totalSpent = tm.getTotalExpense();
        double overallLimit = bm.getOverallLimit();
        int overallProgress = overallLimit > 0 ? (int) ((totalSpent / overallLimit) * 100) : 0;
        if (tvOverallBudget != null) {
            tvOverallBudget.setText(String.format("₹%,.0f / ₹%,.0f", totalSpent, overallLimit));
        }
        if (progressBarOverall != null) {
            progressBarOverall.setProgress(Math.min(overallProgress, 100));
        }

        // Food
        double foodSpent = tm.getCategoryExpense("Food");
        double foodLimit = bm.getBudgetLimit("Food");
        int foodProgress = foodLimit > 0 ? (int) ((foodSpent / foodLimit) * 100) : 0;
        if (tvFoodBudget != null) {
            tvFoodBudget.setText(String.format("₹%,.0f / ₹%,.0f", foodSpent, foodLimit));
        }
        if (progressBarFood != null) {
            progressBarFood.setProgress(Math.min(foodProgress, 100));
        }

        // Transport
        double transportSpent = tm.getCategoryExpense("Transport");
        double transportLimit = bm.getBudgetLimit("Transport");
        int transportProgress = transportLimit > 0 ? (int) ((transportSpent / transportLimit) * 100) : 0;
        if (tvTransportBudget != null) {
            tvTransportBudget.setText(String.format("₹%,.0f / ₹%,.0f", transportSpent, transportLimit));
        }
        if (progressBarTransport != null) {
            progressBarTransport.setProgress(Math.min(transportProgress, 100));
        }

        // Utilities
        double utilitiesSpent = tm.getCategoryExpense("Utilities");
        double utilitiesLimit = bm.getBudgetLimit("Utilities");
        int utilitiesProgress = utilitiesLimit > 0 ? (int) ((utilitiesSpent / utilitiesLimit) * 100) : 0;
        if (tvUtilitiesBudget != null) {
            tvUtilitiesBudget.setText(String.format("₹%,.0f / ₹%,.0f", utilitiesSpent, utilitiesLimit));
        }
        if (progressBarUtilities != null) {
            progressBarUtilities.setProgress(Math.min(utilitiesProgress, 100));
        }
    }

    private void showEditBudgetDialog(String category, double currentLimit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit " + category + " Budget Limit");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter new budget limit");
        input.setText(String.valueOf((int) currentLimit));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = input.getText().toString();
            if (!val.isEmpty()) {
                double newLimit = Double.parseDouble(val);
                if (category.equalsIgnoreCase("Overall")) {
                    BudgetManager.getInstance().setOverallLimit(newLimit);
                } else {
                    BudgetManager.getInstance().setBudgetLimit(category, newLimit);
                }
                updateBudgetData();
                Toast.makeText(getContext(), category + " budget updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
