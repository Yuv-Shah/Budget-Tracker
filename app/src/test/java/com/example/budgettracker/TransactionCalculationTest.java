package com.example.budgettracker;

import com.example.budgettracker.model.Account;
import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.Transaction;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionCalculationTest {

    @Test
    public void testTransactionIncomeExpenseNetCalculations() {
        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction(1, "Salary", "Income", "HDFC Bank", 50000.0, true, "2026-10-01"));
        list.add(new Transaction(2, "Groceries", "Food", "HDFC Bank", 2000.0, false, "2026-10-02"));
        list.add(new Transaction(3, "Restaurant", "Food", "Credit Card", 1500.0, false, "2026-10-03"));

        double incomeSum = 0;
        double expenseSum = 0;
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction t : list) {
            if (t.isIncome()) {
                incomeSum += t.getAmount();
            } else {
                expenseSum += t.getAmount();
                categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }

        Assert.assertEquals(50000.0, incomeSum, 0.001);
        Assert.assertEquals(3500.0, expenseSum, 0.001);
        Assert.assertEquals(46500.0, incomeSum - expenseSum, 0.001);
        Assert.assertEquals(3500.0, categoryMap.get("Food"), 0.001);
    }

    @Test
    public void testAccountBalanceFormula() {
        Account acc = new Account(1, "HDFC Bank", "Savings", 10000.0);
        double initial = acc.getInitialBalance();
        double totalIncome = 50000.0;
        double totalExpense = 2000.0;

        double expectedBalance = initial + totalIncome - totalExpense;
        Assert.assertEquals(58000.0, expectedBalance, 0.001);
    }

    @Test
    public void testTotalNetBalanceIncludingAllAccountInitialBalances() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "HDFC Bank", "Savings", 10000.0));
        accounts.add(new Account(2, "Cash", "Wallet", 5000.0));
        accounts.add(new Account(3, "SBI Bank", "Savings", 15000.0));

        double initialSum = 0;
        for (Account a : accounts) {
            initialSum += a.getInitialBalance();
        }

        double totalIncome = 55000.0;
        double totalExpense = 5950.0;

        double totalNetBalance = initialSum + totalIncome - totalExpense;
        Assert.assertEquals(79050.0, totalNetBalance, 0.001);
    }

    @Test
    public void testCsvImportDeduplicationLogic() {
        List<Transaction> dbList = new ArrayList<>();
        dbList.add(new Transaction(1, "Salary", "Income", "HDFC Bank", 50000.0, true, "Yesterday"));

        List<Transaction> csvIncoming = new ArrayList<>();
        csvIncoming.add(new Transaction(1, "Salary", "Income", "HDFC Bank", 50000.0, true, "Yesterday")); // Duplicate
        csvIncoming.add(new Transaction(2, "Bonus", "Income", "HDFC Bank", 10000.0, true, "Today")); // New

        int newlyAdded = 0;
        int existingUpdated = 0;

        for (Transaction incoming : csvIncoming) {
            boolean exists = false;
            for (Transaction existing : dbList) {
                if (existing.getTitle().equalsIgnoreCase(incoming.getTitle()) &&
                        existing.getCategory().equalsIgnoreCase(incoming.getCategory()) &&
                        existing.getAccountName().equalsIgnoreCase(incoming.getAccountName()) &&
                        existing.getAmount() == incoming.getAmount() &&
                        existing.isIncome() == incoming.isIncome() &&
                        existing.getDate().equalsIgnoreCase(incoming.getDate())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                existingUpdated++;
            } else {
                dbList.add(incoming);
                newlyAdded++;
            }
        }

        Assert.assertEquals(1, newlyAdded);
        Assert.assertEquals(1, existingUpdated);
        Assert.assertEquals(2, dbList.size());

        double totalIncome = 0;
        for (Transaction t : dbList) {
            if (t.isIncome()) totalIncome += t.getAmount();
        }
        Assert.assertEquals(60000.0, totalIncome, 0.001);
    }

    @Test
    public void testBudgetProgressCalculation() {
        Budget b = new Budget("Food", 6000.0);
        double spent = 3500.0;
        int progressPercent = (int) ((spent / b.getLimitAmount()) * 100);

        Assert.assertEquals(58, progressPercent);
    }
}
