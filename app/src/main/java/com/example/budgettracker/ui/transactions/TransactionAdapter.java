package com.example.budgettracker.ui.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.model.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactionList;
    private final OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @SuppressWarnings("notifyDataSetChanged")
    public void updateList(List<Transaction> newList) {
        this.transactionList.clear();
        this.transactionList.addAll(newList);
        notifyDataSetChanged();
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

        View iconBg = holder.itemView.findViewById(R.id.viewTransactionIconBg);
        if (iconBg != null) {
            if (t.isIncome()) {
                iconBg.setBackgroundResource(R.drawable.bg_circle_green);
            } else {
                String cat = t.getCategory().toLowerCase(Locale.ROOT);
                if (cat.contains("food")) {
                    iconBg.setBackgroundResource(R.drawable.bg_circle_purple);
                } else if (cat.contains("travel") || cat.contains("transport")) {
                    iconBg.setBackgroundResource(R.drawable.bg_circle_blue);
                } else if (cat.contains("entertainment")) {
                    iconBg.setBackgroundResource(R.drawable.bg_circle_orange);
                } else if (cat.contains("education") || cat.contains("college")) {
                    iconBg.setBackgroundResource(R.drawable.bg_circle_red);
                } else {
                    iconBg.setBackgroundResource(R.drawable.bg_circle_purple);
                }
            }
        }

        if (t.isIncome()) {
            holder.tvSubtitle.setText(String.format(Locale.getDefault(), "Income (%s) • %s", t.getAccountName(), t.getDate()));
            holder.tvAmount.setText(String.format(Locale.getDefault(), "+₹%,.2f", t.getAmount()));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.income_green));
        } else {
            holder.tvSubtitle.setText(String.format(Locale.getDefault(), "%s • %s • %s", t.getCategory(), t.getAccountName(), t.getDate()));
            holder.tvAmount.setText(String.format(Locale.getDefault(), "-₹%,.2f", t.getAmount()));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.expense_red));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(t);
            }
        });
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
