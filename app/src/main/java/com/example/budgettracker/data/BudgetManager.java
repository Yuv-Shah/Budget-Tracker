package com.example.budgettracker.data;

import com.example.budgettracker.model.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetManager {
    private static BudgetManager instance;
    private final List<Budget> budgets;
    private double overallLimit;

    private BudgetManager() {
        budgets = new ArrayList<>();
        budgets.add(new Budget("Food", 6000.0));
        budgets.add(new Budget("Transport", 4000.0));
        budgets.add(new Budget("Utilities", 3000.0));
        overallLimit = 20000.0;
    }

    public static synchronized BudgetManager getInstance() {
        if (instance == null) {
            instance = new BudgetManager();
        }
        return instance;
    }

    @SuppressWarnings("unused")
    public List<Budget> getBudgets() {
        return budgets;
    }

    public double getOverallLimit() {
        return overallLimit;
    }

    public void setOverallLimit(double overallLimit) {
        this.overallLimit = overallLimit;
    }

    public void setBudgetLimit(String category, double limit) {
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                b.setLimitAmount(limit);
                return;
            }
        }
        budgets.add(new Budget(category, limit));
    }

    public double getBudgetLimit(String category) {
        for (Budget b : budgets) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                return b.getLimitAmount();
            }
        }
        return 5000.0;
    }
}
