package com.example.budgettracker.ui.transactions;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.TransactionManager;
import com.example.budgettracker.model.Transaction;

public class TransactionsFragment extends Fragment {

    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(TransactionManager.getInstance().getTransactions());
        recyclerView.setAdapter(adapter);

        Button btnAdd = view.findViewById(R.id.btnAddTransactionAction);
        btnAdd.setOnClickListener(v -> showAddTransactionDialog());

        return view;
    }

    private void showAddTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Transaction");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_transaction, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etCategory = dialogView.findViewById(R.id.etCategory);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString();
            String amountStr = etAmount.getText().toString();
            String category = etCategory.getText().toString();

            if (!title.isEmpty() && !amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                boolean isIncome = category.equalsIgnoreCase("Income");
                Transaction t = new Transaction(title, category.isEmpty() ? "General" : category, "HDFC Bank", amount, isIncome, "Just now");
                TransactionManager.getInstance().addTransaction(t);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Transaction added successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
