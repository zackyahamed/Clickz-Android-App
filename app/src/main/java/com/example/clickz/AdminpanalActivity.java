package com.example.clickz;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminpanalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminpanal);
        BottomNavigationView bottomNav = findViewById(R.id.adminBottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashBoardFragment();
            } else if (itemId == R.id.nav_products) {
                selectedFragment = new ProductManagementFragment();
            } else if (itemId == R.id.nav_users) {
                selectedFragment = new ManageUsersFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminFragmentContainer, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load default fragment (Dashboard)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminFragmentContainer, new DashBoardFragment())
                .commit();




    }
}