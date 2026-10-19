package com.example.budgettracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.ui.budgets.BudgetsFragment;
import com.example.budgettracker.ui.dashboard.DashboardFragment;
import com.example.budgettracker.ui.settings.SettingsFragment;
import com.example.budgettracker.ui.statistics.StatisticsFragment;
import com.example.budgettracker.ui.transactions.TransactionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.navHostFragment, new DashboardFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.navigation_transactions) {
                selectedFragment = new TransactionsFragment();
            } else if (itemId == R.id.navigation_budgets) {
                selectedFragment = new BudgetsFragment();
            } else if (itemId == R.id.navigation_statistics) {
                selectedFragment = new StatisticsFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.navHostFragment, selectedFragment)
                    .commit();
            return true;
        });
    }
}
