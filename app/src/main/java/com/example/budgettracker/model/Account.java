package com.example.budgettracker.model;

public class Account {
    private long id;
    private String name;
    private String type;
    private double initialBalance;

    public Account(long id, String name, String type, double initialBalance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
    }

    public Account(String name, String type, double initialBalance) {
        this(-1, name, type, initialBalance);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }
}
