package com.example.budgettracker.data;

import com.example.budgettracker.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static TransactionManager instance;
    private final List<Transaction> transactions;

    private TransactionManager() {
        transactions = new ArrayList<>();
        transactions.add(new Transaction("Grocery Shopping", "Food", "HDFC Bank", 1250.0, false, "Today, 2:30 PM"));
        transactions.add(new Transaction("Monthly Salary", "Income", "Savings", 55000.0, true, "Yesterday"));
        transactions.add(new Transaction("Electricity Bill", "Utilities", "Credit Card", 2400.0, false, "3 days ago"));
        transactions.add(new Transaction("Fuel / Petrol", "Transport", "Cash", 800.0, false, "4 days ago"));
        transactions.add(new Transaction("Restaurant Dining", "Food", "Credit Card", 1500.0, false, "5 days ago"));
    }

    public static synchronized TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(0, transaction);
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public double getCategoryExpense(String category) {
        return transactions.stream()
                .filter(t -> !t.isIncome() && t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getAccountBalance(String accountName) {
        double income = transactions.stream()
                .filter(t -> t.getAccountName().equalsIgnoreCase(accountName) && t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expense = transactions.stream()
                .filter(t -> t.getAccountName().equalsIgnoreCase(accountName) && !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
        return 10000.0 + income - expense;
    }
}
