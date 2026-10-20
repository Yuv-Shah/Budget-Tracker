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
import com.example.budgettracker.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    private Spinner spinnerDateRange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        spinnerDateRange = view.findViewById(R.id.spinnerDateRange);
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
        String selectedRange = "All Time";
        if (spinnerDateRange != null && spinnerDateRange.getSelectedItem() != null) {
            selectedRange = spinnerDateRange.getSelectedItem().toString();
        }

        List<Transaction> transactions = TransactionManager.getInstance().getTransactionsForDateRange(selectedRange);

        double incomeSum = 0;
        double expenseSum = 0;
        Map<String, Double> categoryExpenses = new HashMap<>();
        List<Double> trendList = new ArrayList<>();

        // Loop over transactions for range
        for (Transaction t : transactions) {
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

        // Reverse trend list so chronological order goes left to right
        Collections.reverse(trendList);

        double net = incomeSum - expenseSum;

        if (tvStatIncome != null) tvStatIncome.setText("+" + CurrencyUtils.formatRupee(incomeSum));
        if (tvStatExpense != null) tvStatExpense.setText("-" + CurrencyUtils.formatRupee(expenseSum));
        if (tvStatNet != null) {
            tvStatNet.setText(CurrencyUtils.formatRupee(net));
            tvStatNet.setTextColor(net >= 0 ? 0xFF34D399 : 0xFFF87171);
        }

        if (tvIncomeBarVal != null) tvIncomeBarVal.setText(CurrencyUtils.formatRupeeNoDecimals(incomeSum));
        if (tvExpenseBarVal != null) tvExpenseBarVal.setText(CurrencyUtils.formatRupeeNoDecimals(expenseSum));

        double maxVal = Math.max(incomeSum, expenseSum);
        if (maxVal > 0) {
            float density = getResources().getDisplayMetrics().density;
            if (barIncome != null) {
                ViewGroup.LayoutParams lp = barIncome.getLayoutParams();
                lp.height = (int) Math.max(30, (incomeSum / maxVal) * 160 * density);
                barIncome.setLayoutParams(lp);
            }
            if (barExpense != null) {
                ViewGroup.LayoutParams lp = barExpense.getLayoutParams();
                lp.height = (int) Math.max(30, (expenseSum / maxVal) * 160 * density);
                barExpense.setLayoutParams(lp);
            }
        }

        // Update Pie/Donut Chart & Slice Click Listener
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
                tv.setText(String.format(Locale.getDefault(), "• %s: %s (%.1f%%)", cat, CurrencyUtils.formatRupee(amt), percentage));
                tv.setTextColor(getResources().getColor(R.color.text_primary, null));
                tv.setTextSize(14f);
                tv.setPadding(0, 12, 0, 12);
                tv.setClickable(true);
                tv.setFocusable(true);
                tv.setBackgroundResource(android.R.drawable.list_selector_background);

                tv.setOnClickListener(v -> showCategoryTransactionsDialog(cat));

                layoutCategoryBreakdown.addView(tv);
            }
            if (categoryExpenses.isEmpty()) {
                TextView tv = new TextView(getContext());
                tv.setText("No expense records found for selected period.");
                tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
                tv.setPadding(0, 12, 0, 12);
                layoutCategoryBreakdown.addView(tv);
            }
        }
    }

    private void showCategoryTransactionsDialog(String categoryName) {
        String selectedRange = "All Time";
        if (spinnerDateRange != null && spinnerDateRange.getSelectedItem() != null) {
            selectedRange = spinnerDateRange.getSelectedItem().toString();
        }

        List<Transaction> all = TransactionManager.getInstance().getTransactionsForDateRange(selectedRange);
        List<String> matched = new ArrayList<>();
        for (Transaction t : all) {
            if (!t.isIncome() && t.getCategory().equalsIgnoreCase(categoryName)) {
                matched.add(t.getTitle() + " - " + CurrencyUtils.formatRupee(t.getAmount()) + " (" + t.getDate() + ")");
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
