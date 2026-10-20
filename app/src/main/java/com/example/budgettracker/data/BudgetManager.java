package com.example.budgettracker.data;

import com.example.budgettracker.BudgetTrackerApp;
import com.example.budgettracker.model.Budget;

import java.util.List;

public class BudgetManager {
    private static BudgetManager instance;

    private BudgetManager() {
    }

    public static synchronized BudgetManager getInstance() {
        if (instance == null) {
            instance = new BudgetManager();
        }
        return instance;
    }

    private AppDatabaseHelper getDb() {
        return AppDatabaseHelper.getInstance(BudgetTrackerApp.getContext());
    }

    public List<Budget> getBudgets() {
        return getDb().getAllBudgets();
    }

    public double getOverallLimit() {
        return getDb().getOverallLimit();
    }

    public void setOverallLimit(double overallLimit) {
        getDb().setOverallLimit(overallLimit);
    }

    public void setBudgetLimit(String category, double limit) {
        getDb().setBudgetLimit(category, limit);
    }

    public double getBudgetLimit(String category) {
        return getDb().getBudgetLimit(category);
    }
}
