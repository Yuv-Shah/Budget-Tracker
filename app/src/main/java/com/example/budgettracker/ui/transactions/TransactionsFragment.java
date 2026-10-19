package com.example.budgettracker.ui.transactions;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Transaction;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionsFragment extends Fragment {

    private TransactionAdapter adapter;
    private TabLayout tabLayout;
    private EditText etSearch;
    private final List<Transaction> displayedTransactions = new ArrayList<>();
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutTransactions);
        etSearch = view.findViewById(R.id.etSearch);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(displayedTransactions, this::showTransactionDetailsDialog);
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTransactions();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().toLowerCase(Locale.ROOT).trim();
                    filterTransactions();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        Button btnAdd = view.findViewById(R.id.btnAddTransactionAction);
        btnAdd.setOnClickListener(v ->
                TransactionDialogHelper.showAddTransactionDialog(requireContext(), isIncome -> {
                    if (isIncome && tabLayout.getSelectedTabPosition() != 1) {
                        TabLayout.Tab tab = tabLayout.getTabAt(1);
                        if (tab != null) tab.select();
                    } else if (!isIncome && tabLayout.getSelectedTabPosition() != 0) {
                        TabLayout.Tab tab = tabLayout.getTabAt(0);
                        if (tab != null) tab.select();
                    } else {
                        filterTransactions();
                    }
                })
        );

        View btnDemoListView = view.findViewById(R.id.btnDemoListView);
        if (btnDemoListView != null) {
            btnDemoListView.setOnClickListener(v -> showListViewDemoDialog());
        }

        filterTransactions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        filterTransactions();
    }

    private void filterTransactions() {
        if (tabLayout == null) return;
        boolean showIncome = tabLayout.getSelectedTabPosition() == 1;

        List<Transaction> all = TransactionManager.getInstance().getTransactions();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            boolean matchesType = (t.isIncome() == showIncome);
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    t.getTitle().toLowerCase(Locale.ROOT).contains(currentSearchQuery) ||
                    t.getCategory().toLowerCase(Locale.ROOT).contains(currentSearchQuery) ||
                    t.getAccountName().toLowerCase(Locale.ROOT).contains(currentSearchQuery);

            if (matchesType && matchesSearch) {
                filtered.add(t);
            }
        }
        adapter.updateList(filtered);
    }

    private void showTransactionDetailsDialog(Transaction t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Transaction Details");

        String details = String.format(Locale.getDefault(),
                "Title: %s\nType: %s\nAmount: ₹%,.2f\nCategory: %s\nAccount: %s\nDate: %s",
                t.getTitle(),
                t.isIncome() ? "Income" : "Expense",
                t.getAmount(),
                t.getCategory(),
                t.getAccountName(),
                t.getDate());

        builder.setMessage(details);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Delete", (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Yes", (d, w) -> {
                        TransactionManager.getInstance().getTransactions().remove(t);
                        filterTransactions();
                        Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        builder.show();
    }

    private void showListViewDemoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Course Demo: ListView + SimpleAdapter");

        ListView listView = new ListView(requireContext());
        List<Map<String, String>> data = new ArrayList<>();
        for (Transaction t : TransactionManager.getInstance().getTransactions()) {
            Map<String, String> map = new HashMap<>();
            map.put("title", t.getTitle() + " (" + t.getCategory() + ")");
            map.put("subtitle", "₹" + t.getAmount() + " • " + t.getAccountName() + " • " + t.getDate());
            data.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                requireContext(),
                data,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        listView.setAdapter(simpleAdapter);
        builder.setView(listView);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
