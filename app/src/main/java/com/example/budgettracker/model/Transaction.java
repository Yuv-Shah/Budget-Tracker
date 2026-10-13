package com.example.budgettracker.model;

public class Transaction {
    private String title;
    private String category;
    private String accountName;
    private double amount;
    private boolean isIncome;
    private String date;

    public Transaction(String title, String category, String accountName, double amount, boolean isIncome, String date) {
        this.title = title;
        this.category = category;
        this.accountName = accountName;
        this.amount = amount;
        this.isIncome = isIncome;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getAccountName() { return accountName; }
    public double getAmount() { return amount; }
    public boolean isIncome() { return isIncome; }
    public String getDate() { return date; }
}
