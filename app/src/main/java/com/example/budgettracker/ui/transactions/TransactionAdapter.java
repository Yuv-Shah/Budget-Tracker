package com.example.budgettracker.ui.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactionList.get(position);
        holder.tvTitle.setText(t.getTitle());
        holder.tvSubtitle.setText(t.getCategory() + " • " + t.getAccountName() + " • " + t.getDate());

        if (t.isIncome()) {
            holder.tvAmount.setText("+₹" + String.format("%,.2f", t.getAmount()));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.income_green));
        } else {
            holder.tvAmount.setText("-₹" + String.format("%,.2f", t.getAmount()));
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.expense_red));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
