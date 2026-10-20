package com.example.budgettracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.budgettracker.model.Account;
import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budget_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Transactions
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TRANS_ID = "id";
    public static final String COL_TRANS_TITLE = "title";
    public static final String COL_TRANS_CATEGORY = "category";
    public static final String COL_TRANS_ACCOUNT = "account_name";
    public static final String COL_TRANS_AMOUNT = "amount";
    public static final String COL_TRANS_IS_INCOME = "is_income";
    public static final String COL_TRANS_DATE = "date";
    public static final String COL_TRANS_TIMESTAMP = "timestamp";

    // Table Accounts
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String COL_ACC_ID = "id";
    public static final String COL_ACC_NAME = "name";
    public static final String COL_ACC_TYPE = "type";
    public static final String COL_ACC_INITIAL_BALANCE = "initial_balance";

    // Table Budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COL_BUDGET_ID = "id";
    public static final String COL_BUDGET_CATEGORY = "category";
    public static final String COL_BUDGET_LIMIT = "limit_amount";

    private static AppDatabaseHelper instance;

    public static synchronized AppDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TRANS_TITLE + " TEXT NOT NULL, " +
                COL_TRANS_CATEGORY + " TEXT NOT NULL, " +
                COL_TRANS_ACCOUNT + " TEXT NOT NULL, " +
                COL_TRANS_AMOUNT + " REAL NOT NULL, " +
                COL_TRANS_IS_INCOME + " INTEGER NOT NULL, " +
                COL_TRANS_DATE + " TEXT NOT NULL, " +
                COL_TRANS_TIMESTAMP + " INTEGER NOT NULL)";

        String createAccountsTable = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COL_ACC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACC_NAME + " TEXT UNIQUE NOT NULL, " +
                COL_ACC_TYPE + " TEXT NOT NULL, " +
                COL_ACC_INITIAL_BALANCE + " REAL NOT NULL)";

        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + " (" +
                COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BUDGET_CATEGORY + " TEXT UNIQUE NOT NULL, " +
                COL_BUDGET_LIMIT + " REAL NOT NULL)";

        db.execSQL(createTransactionsTable);
        db.execSQL(createAccountsTable);
        db.execSQL(createBudgetsTable);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Seed default Accounts
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COL_ACC_NAME + ", " + COL_ACC_TYPE + ", " + COL_ACC_INITIAL_BALANCE + ") VALUES ('HDFC Bank', 'Savings Account', 10000.0)");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COL_ACC_NAME + ", " + COL_ACC_TYPE + ", " + COL_ACC_INITIAL_BALANCE + ") VALUES ('Cash', 'Cash / Wallet', 5000.0)");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COL_ACC_NAME + ", " + COL_ACC_TYPE + ", " + COL_ACC_INITIAL_BALANCE + ") VALUES ('Credit Card', 'Credit Card', 0.0)");

        // Seed default Budgets
        db.execSQL("INSERT INTO " + TABLE_BUDGETS + " (" + COL_BUDGET_CATEGORY + ", " + COL_BUDGET_LIMIT + ") VALUES ('Overall', 20000.0)");
        db.execSQL("INSERT INTO " + TABLE_BUDGETS + " (" + COL_BUDGET_CATEGORY + ", " + COL_BUDGET_LIMIT + ") VALUES ('Food', 6000.0)");
        db.execSQL("INSERT INTO " + TABLE_BUDGETS + " (" + COL_BUDGET_CATEGORY + ", " + COL_BUDGET_LIMIT + ") VALUES ('Transport', 4000.0)");
        db.execSQL("INSERT INTO " + TABLE_BUDGETS + " (" + COL_BUDGET_CATEGORY + ", " + COL_BUDGET_LIMIT + ") VALUES ('Utilities', 3000.0)");

        // Seed default Transactions
        long now = System.currentTimeMillis();
        long day = 86400000L;

        insertTransactionInternal(db, "Grocery Shopping", "Food", "HDFC Bank", 1250.0, 0, "Today, 2:30 PM", now);
        insertTransactionInternal(db, "Monthly Salary", "Income", "HDFC Bank", 55000.0, 1, "Yesterday", now - day);
        insertTransactionInternal(db, "Electricity Bill", "Utilities", "Credit Card", 2400.0, 0, "3 days ago", now - (3 * day));
        insertTransactionInternal(db, "Fuel / Petrol", "Transport", "Cash", 800.0, 0, "4 days ago", now - (4 * day));
        insertTransactionInternal(db, "Restaurant Dining", "Food", "Credit Card", 1500.0, 0, "5 days ago", now - (5 * day));
    }

    private void insertTransactionInternal(SQLiteDatabase db, String title, String category, String account, double amount, int isIncome, String date, long timestamp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TRANS_TITLE, title);
        cv.put(COL_TRANS_CATEGORY, category);
        cv.put(COL_TRANS_ACCOUNT, account);
        cv.put(COL_TRANS_AMOUNT, amount);
        cv.put(COL_TRANS_IS_INCOME, isIncome);
        cv.put(COL_TRANS_DATE, date);
        cv.put(COL_TRANS_TIMESTAMP, timestamp);
        db.insert(TABLE_TRANSACTIONS, null, cv);
    }

    // --- TRANSACTION METHODS ---

    public long insertTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TRANS_TITLE, t.getTitle());
        cv.put(COL_TRANS_CATEGORY, t.getCategory());
        cv.put(COL_TRANS_ACCOUNT, t.getAccountName());
        cv.put(COL_TRANS_AMOUNT, t.getAmount());
        cv.put(COL_TRANS_IS_INCOME, t.isIncome() ? 1 : 0);
        cv.put(COL_TRANS_DATE, t.getDate());
        cv.put(COL_TRANS_TIMESTAMP, System.currentTimeMillis());
        long id = db.insert(TABLE_TRANSACTIONS, null, cv);
        t.setId(id);
        return id;
    }

    public boolean insertOrUpdateTransactionFromCsv(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COL_TRANS_TITLE + "=? AND " +
                COL_TRANS_CATEGORY + "=? AND " +
                COL_TRANS_ACCOUNT + "=? AND " +
                COL_TRANS_AMOUNT + "=? AND " +
                COL_TRANS_IS_INCOME + "=? AND " +
                COL_TRANS_DATE + "=?";

        String[] selectionArgs = new String[]{
                t.getTitle(),
                t.getCategory(),
                t.getAccountName(),
                String.valueOf(t.getAmount()),
                t.isIncome() ? "1" : "0",
                t.getDate()
        };

        Cursor cursor = db.query(TABLE_TRANSACTIONS, new String[]{COL_TRANS_ID}, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();

        if (exists) {
            ContentValues cv = new ContentValues();
            cv.put(COL_TRANS_TIMESTAMP, System.currentTimeMillis());
            db.update(TABLE_TRANSACTIONS, cv, selection, selectionArgs);
            return false; // false = updated existing
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_TRANS_TITLE, t.getTitle());
            cv.put(COL_TRANS_CATEGORY, t.getCategory());
            cv.put(COL_TRANS_ACCOUNT, t.getAccountName());
            cv.put(COL_TRANS_AMOUNT, t.getAmount());
            cv.put(COL_TRANS_IS_INCOME, t.isIncome() ? 1 : 0);
            cv.put(COL_TRANS_DATE, t.getDate());
            cv.put(COL_TRANS_TIMESTAMP, System.currentTimeMillis());
            long id = db.insert(TABLE_TRANSACTIONS, null, cv);
            t.setId(id);
            return true; // true = newly inserted
        }
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
        db.delete(TABLE_ACCOUNTS, null, null);
        db.delete(TABLE_BUDGETS, null, null);
    }

    public boolean deleteTransaction(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TRANSACTIONS, COL_TRANS_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public List<Transaction> getAllTransactions() {
        return getTransactionsForDateRange("All Time");
    }

    public List<Transaction> getTransactionsForDateRange(String dateRange) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        long minTimestamp = getMinTimestampForRange(dateRange);

        String selection = null;
        String[] selectionArgs = null;
        if (minTimestamp > 0) {
            selection = COL_TRANS_TIMESTAMP + " >= ?";
            selectionArgs = new String[]{String.valueOf(minTimestamp)};
        }

        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, selection, selectionArgs, null, null, COL_TRANS_TIMESTAMP + " DESC, " + COL_TRANS_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANS_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_TITLE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_CATEGORY));
                String account = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_ACCOUNT));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANS_AMOUNT));
                boolean isIncome = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANS_IS_INCOME)) == 1;
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANS_DATE));

                list.add(new Transaction(id, title, category, account, amount, isIncome, date));
            }
            cursor.close();
        }
        return list;
    }

    public double getTotalIncome(String dateRange) {
        return getSumForType(1, dateRange);
    }

    public double getTotalExpense(String dateRange) {
        return getSumForType(0, dateRange);
    }

    private double getSumForType(int isIncome, String dateRange) {
        SQLiteDatabase db = this.getReadableDatabase();
        long minTimestamp = getMinTimestampForRange(dateRange);

        String sql = "SELECT SUM(" + COL_TRANS_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_IS_INCOME + " = " + isIncome;
        List<String> argsList = new ArrayList<>();
        if (minTimestamp > 0) {
            sql += " AND " + COL_TRANS_TIMESTAMP + " >= ?";
            argsList.add(String.valueOf(minTimestamp));
        }

        Cursor cursor = db.rawQuery(sql, argsList.toArray(new String[0]));
        double sum = 0;
        if (cursor != null && cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
            cursor.close();
        }
        return sum;
    }

    public double getTotalBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        double initialSum = 0;
        Cursor c = db.rawQuery("SELECT SUM(" + COL_ACC_INITIAL_BALANCE + ") FROM " + TABLE_ACCOUNTS, null);
        if (c != null) {
            if (c.moveToFirst()) {
                initialSum = c.getDouble(0);
            }
            c.close();
        }
        return initialSum + getTotalIncome("All Time") - getTotalExpense("All Time");
    }

    public Map<String, Double> getCategoryExpenses(String dateRange) {
        Map<String, Double> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        long minTimestamp = getMinTimestampForRange(dateRange);

        String sql = "SELECT " + COL_TRANS_CATEGORY + ", SUM(" + COL_TRANS_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COL_TRANS_IS_INCOME + " = 0";
        List<String> argsList = new ArrayList<>();
        if (minTimestamp > 0) {
            sql += " AND " + COL_TRANS_TIMESTAMP + " >= ?";
            argsList.add(String.valueOf(minTimestamp));
        }
        sql += " GROUP BY " + COL_TRANS_CATEGORY;

        Cursor cursor = db.rawQuery(sql, argsList.toArray(new String[0]));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(0);
                double amount = cursor.getDouble(1);
                map.put(cat, amount);
            }
            cursor.close();
        }
        return map;
    }

    public double getCategoryExpense(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT SUM(" + COL_TRANS_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COL_TRANS_IS_INCOME + " = 0 AND " + COL_TRANS_CATEGORY + " = ? COLLATE NOCASE";
        Cursor cursor = db.rawQuery(sql, new String[]{category});
        double sum = 0;
        if (cursor != null && cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
            cursor.close();
        }
        return sum;
    }

    // --- ACCOUNT METHODS ---

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, null, null, null, null, null, COL_ACC_ID + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACC_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACC_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACC_TYPE));
                double initBal = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ACC_INITIAL_BALANCE));
                list.add(new Account(id, name, type, initBal));
            }
            cursor.close();
        }
        return list;
    }

    public long insertOrUpdateAccount(String name, String type, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ACC_NAME, name);
        cv.put(COL_ACC_TYPE, type);
        cv.put(COL_ACC_INITIAL_BALANCE, balance);

        int updated = db.update(TABLE_ACCOUNTS, cv, COL_ACC_NAME + "=? COLLATE NOCASE", new String[]{name});
        if (updated > 0) {
            return updated;
        } else {
            return db.insert(TABLE_ACCOUNTS, null, cv);
        }
    }

    public boolean deleteAccountByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ACCOUNTS, COL_ACC_NAME + "=? COLLATE NOCASE", new String[]{name});
        return rows > 0;
    }

    public double getAccountBalance(String accountName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Initial balance
        double initial = 0.0;
        Cursor cAcc = db.query(TABLE_ACCOUNTS, new String[]{COL_ACC_INITIAL_BALANCE}, COL_ACC_NAME + "=? COLLATE NOCASE", new String[]{accountName}, null, null, null);
        if (cAcc != null && cAcc.moveToFirst()) {
            initial = cAcc.getDouble(0);
            cAcc.close();
        }

        // 2. Income for account
        double income = 0.0;
        Cursor cInc = db.rawQuery("SELECT SUM(" + COL_TRANS_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_ACCOUNT + "=? COLLATE NOCASE AND " + COL_TRANS_IS_INCOME + "=1", new String[]{accountName});
        if (cInc != null && cInc.moveToFirst()) {
            income = cInc.getDouble(0);
            cInc.close();
        }

        // 3. Expense for account
        double expense = 0.0;
        Cursor cExp = db.rawQuery("SELECT SUM(" + COL_TRANS_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TRANS_ACCOUNT + "=? COLLATE NOCASE AND " + COL_TRANS_IS_INCOME + "=0", new String[]{accountName});
        if (cExp != null && cExp.moveToFirst()) {
            expense = cExp.getDouble(0);
            cExp.close();
        }

        return initial + income - expense;
    }

    // --- BUDGET METHODS ---

    public void setBudgetLimit(String category, double limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BUDGET_CATEGORY, category);
        cv.put(COL_BUDGET_LIMIT, limit);

        int updated = db.update(TABLE_BUDGETS, cv, COL_BUDGET_CATEGORY + "=? COLLATE NOCASE", new String[]{category});
        if (updated == 0) {
            db.insert(TABLE_BUDGETS, null, cv);
        }
    }

    public double getBudgetLimit(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUDGETS, new String[]{COL_BUDGET_LIMIT}, COL_BUDGET_CATEGORY + "=? COLLATE NOCASE", new String[]{category}, null, null, null);
        double limit = 5000.0; // Default fallback
        if (cursor != null && cursor.moveToFirst()) {
            limit = cursor.getDouble(0);
            cursor.close();
        }
        return limit;
    }

    public double getOverallLimit() {
        return getBudgetLimit("Overall");
    }

    public void setOverallLimit(double limit) {
        setBudgetLimit("Overall", limit);
    }

    public List<Budget> getAllBudgets() {
        List<Budget> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUDGETS, null, COL_BUDGET_CATEGORY + " != 'Overall'", null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String cat = cursor.getString(cursor.getColumnIndexOrThrow(COL_BUDGET_CATEGORY));
                double limit = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BUDGET_LIMIT));
                list.add(new Budget(cat, limit));
            }
            cursor.close();
        }
        return list;
    }

    // --- HELPER DATE RANGE ---

    private long getMinTimestampForRange(String dateRange) {
        if (dateRange == null || dateRange.equalsIgnoreCase("All Time")) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (dateRange.equalsIgnoreCase("This Month")) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTimeInMillis();
        } else if (dateRange.equalsIgnoreCase("Last Month")) {
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTimeInMillis();
        } else if (dateRange.equalsIgnoreCase("This Year")) {
            cal.set(Calendar.DAY_OF_YEAR, 1);
            return cal.getTimeInMillis();
        }
        return 0;
    }
}
