package com.example.budgettracker.data;

import com.example.budgettracker.BudgetTrackerApp;
import com.example.budgettracker.model.Transaction;

import java.util.List;
import java.util.Map;

public class TransactionManager {
    private static TransactionManager instance;

    private TransactionManager() {
    }

    public static synchronized TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    private AppDatabaseHelper getDb() {
        return AppDatabaseHelper.getInstance(BudgetTrackerApp.getContext());
    }

    public List<Transaction> getTransactions() {
        return getDb().getAllTransactions();
    }

    public List<Transaction> getTransactionsForDateRange(String dateRange) {
        return getDb().getTransactionsForDateRange(dateRange);
    }

    public void addTransaction(Transaction transaction) {
        getDb().insertTransaction(transaction);
    }

    public boolean addOrUpdateTransactionFromCsv(Transaction transaction) {
        return getDb().insertOrUpdateTransactionFromCsv(transaction);
    }

    public void deleteAllTransactions() {
        getDb().deleteAllTransactions();
    }

    public boolean deleteTransaction(long id) {
        return getDb().deleteTransaction(id);
    }

    public double getTotalIncome() {
        return getDb().getTotalIncome("All Time");
    }

    public double getTotalIncome(String dateRange) {
        return getDb().getTotalIncome(dateRange);
    }

    public double getTotalExpense() {
        return getDb().getTotalExpense("All Time");
    }

    public double getTotalExpense(String dateRange) {
        return getDb().getTotalExpense(dateRange);
    }

    public double getTotalBalance() {
        return getDb().getTotalBalance();
    }

    public double getCategoryExpense(String category) {
        return getDb().getCategoryExpense(category);
    }

    public Map<String, Double> getCategoryExpenses(String dateRange) {
        return getDb().getCategoryExpenses(dateRange);
    }

    public double getAccountBalance(String accountName) {
        return getDb().getAccountBalance(accountName);
    }
}
