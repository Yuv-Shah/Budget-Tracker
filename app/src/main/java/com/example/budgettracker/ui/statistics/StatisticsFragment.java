package com.example.budgettracker.ui.statistics;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.R;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private TextView tvStatIncome, tvStatExpense, tvStatNet, tvIncomeBarVal, tvExpenseBarVal;
    private LinearLayout layoutCategoryBreakdown;
    private View barIncome, barExpense;
    private DonutChartView donutChartView;
    private LineChartView lineChartView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        Spinner spinnerDateRange = view.findViewById(R.id.spinnerDateRange);
        tvStatIncome = view.findViewById(R.id.tvStatIncome);
        tvStatExpense = view.findViewById(R.id.tvStatExpense);
        tvStatNet = view.findViewById(R.id.tvStatNet);
        tvIncomeBarVal = view.findViewById(R.id.tvIncomeBarVal);
        tvExpenseBarVal = view.findViewById(R.id.tvExpenseBarVal);
        layoutCategoryBreakdown = view.findViewById(R.id.layoutCategoryBreakdown);
        barIncome = view.findViewById(R.id.barIncome);
        barExpense = view.findViewById(R.id.barExpense);
        donutChartView = view.findViewById(R.id.donutChartView);
        lineChartView = view.findViewById(R.id.lineChartView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.date_range_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateRange.setAdapter(adapter);

        spinnerDateRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                updateStatisticsData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateStatisticsData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatisticsData();
    }

    private void updateStatisticsData() {
        List<Transaction> allTransactions = TransactionManager.getInstance().getTransactions();

        double incomeSum = 0;
        double expenseSum = 0;
        Map<String, Double> categoryExpenses = new HashMap<>();
        List<Double> trendList = new ArrayList<>();

        for (Transaction t : allTransactions) {
            if (t.isIncome()) {
                incomeSum += t.getAmount();
            } else {
                expenseSum += t.getAmount();
                String cat = t.getCategory();
                Double current = categoryExpenses.get(cat);
                if (current == null) {
                    current = 0.0;
                }
                categoryExpenses.put(cat, current + t.getAmount());
                trendList.add(t.getAmount());
            }
        }

        double net = incomeSum - expenseSum;

        if (tvStatIncome != null) tvStatIncome.setText(String.format(Locale.getDefault(), "+₹%,.2f", incomeSum));
        if (tvStatExpense != null) tvStatExpense.setText(String.format(Locale.getDefault(), "-₹%,.2f", expenseSum));
        if (tvStatNet != null) {
            tvStatNet.setText(String.format(Locale.getDefault(), "₹%,.2f", net));
            tvStatNet.setTextColor(net >= 0 ? 0xFF34D399 : 0xFFF87171);
        }

        if (tvIncomeBarVal != null) tvIncomeBarVal.setText(String.format(Locale.getDefault(), "₹%,.0f", incomeSum));
        if (tvExpenseBarVal != null) tvExpenseBarVal.setText(String.format(Locale.getDefault(), "₹%,.0f", expenseSum));

        double maxVal = Math.max(incomeSum, expenseSum);
        if (maxVal > 0) {
            if (barIncome != null) {
                ViewGroup.LayoutParams lp = barIncome.getLayoutParams();
                lp.height = (int) Math.max(30, (incomeSum / maxVal) * 160 * getResources().getDisplayMetrics().density);
                barIncome.setLayoutParams(lp);
            }
            if (barExpense != null) {
                ViewGroup.LayoutParams lp = barExpense.getLayoutParams();
                lp.height = (int) Math.max(30, (expenseSum / maxVal) * 160 * getResources().getDisplayMetrics().density);
                barExpense.setLayoutParams(lp);
            }
        }

        // Update Pie Chart & Slice Click Listener
        if (donutChartView != null) {
            donutChartView.setData(categoryExpenses);
            donutChartView.setOnSliceClickListener(this::showCategoryTransactionsDialog);
        }

        // Update Line Chart
        if (lineChartView != null) {
            lineChartView.setTrendData(trendList);
        }

        if (layoutCategoryBreakdown != null) {
            layoutCategoryBreakdown.removeAllViews();
            for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
                String cat = entry.getKey();
                double amt = entry.getValue();
                double percentage = expenseSum > 0 ? (amt / expenseSum) * 100 : 0;

                TextView tv = new TextView(getContext());
                tv.setText(String.format(Locale.getDefault(), "• %s: ₹%,.2f (%.1f%%)", cat, amt, percentage));
                tv.setTextColor(getResources().getColor(R.color.text_primary, null));
                tv.setTextSize(14f);
                tv.setPadding(0, 8, 0, 8);
                tv.setClickable(true);
                tv.setFocusable(true);
                tv.setBackgroundResource(android.R.drawable.list_selector_background);

                tv.setOnClickListener(v -> showCategoryTransactionsDialog(cat));

                layoutCategoryBreakdown.addView(tv);
            }
            if (categoryExpenses.isEmpty()) {
                TextView tv = new TextView(getContext());
                tv.setText("No expense records found.");
                tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
                layoutCategoryBreakdown.addView(tv);
            }
        }
    }

    private void showCategoryTransactionsDialog(String categoryName) {
        List<Transaction> all = TransactionManager.getInstance().getTransactions();
        List<String> matched = new ArrayList<>();
        for (Transaction t : all) {
            if (!t.isIncome() && t.getCategory().equalsIgnoreCase(categoryName)) {
                matched.add(t.getTitle() + " - ₹" + String.format(Locale.getDefault(), "%,.2f", t.getAmount()) + " (" + t.getDate() + ")");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Transactions for " + categoryName);
        if (matched.isEmpty()) {
            builder.setMessage("No transactions found for this category.");
        } else {
            builder.setItems(matched.toArray(new String[0]), null);
        }
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
