package com.example.clickz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clickz.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

         auth = FirebaseAuth.getInstance();
  db = FirebaseFirestore.getInstance();


         drawer = binding.drawerLayout;
        navigationView = binding.navView;

        showAdminMenuItem(false);




        String navigateTo = getIntent().getStringExtra("navigateTo");
        if (navigateTo != null && navigateTo.equals("home")) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
            navController.navigate(R.id.nav_home);
        }
        Log.d("CliHomeActivity", "Navigating to Home Fragment");



        View headerView = navigationView.getHeaderView(0);

        TextView usernameTextView = headerView.findViewById(R.id.navHeaderTextView);
        TextView emailTextView = headerView.findViewById(R.id.nav_emailTextView);



        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            String firstName = sharedPreferences.getString("user_fname", "Clickz");
            String lastName = sharedPreferences.getString("user_lname", "");
            String Uemail = sharedPreferences.getString("email", "Sign In");

            String fullName = firstName + " " + lastName;

            usernameTextView.setText(fullName);
            emailTextView.setText(Uemail);

            checkAdminStatus();
            navigationView.post(new Runnable() {
                @Override
                public void run() {
                    navigationView.invalidate();
                }
            });

        } else {

            usernameTextView.setText("Clickz");
            emailTextView.setText("Sign In");
            showAdminMenuItem(false);
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_profile,R.id.nav_add_product,R.id.nav_wishList,R.id.contact_us)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    private void checkAdminStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String Uemail = sharedPreferences.getString("email", "Sign In");

        db.collection("admin").whereEqualTo("email",Uemail).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {

                        showAdminMenuItem(false);
                    } else {

                        showAdminMenuItem(true);
                    }
                })
                .addOnFailureListener(e -> showAdminMenuItem(false));
    }
    private void showAdminMenuItem(boolean isAdmin) {
        Menu menu = navigationView.getMenu();
        MenuItem addproduct= menu.findItem(R.id.nav_add_product);
        MenuItem adminpanal = menu.findItem(R.id.nav_adminpanal);
        addproduct.setVisible(isAdmin);
        adminpanal.setVisible(isAdmin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void setToolbarTitle(String title) {
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }
}