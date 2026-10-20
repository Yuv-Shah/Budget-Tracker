package com.example.budgettracker.model;

public class Transaction {
    private long id;
    private final String title;
    private final String category;
    private final String accountName;
    private final double amount;
    private final boolean isIncome;
    private final String date;

    public Transaction(long id, String title, String category, String accountName, double amount, boolean isIncome, String date) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.accountName = accountName;
        this.amount = amount;
        this.isIncome = isIncome;
        this.date = date;
    }

    public Transaction(String title, String category, String accountName, double amount, boolean isIncome, String date) {
        this(-1, title, category, accountName, amount, isIncome, date);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getAccountName() { return accountName; }
    public double getAmount() { return amount; }
    public boolean isIncome() { return isIncome; }
    public String getDate() { return date; }
}
