package com.example.clickz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import model.User;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView createAccount= findViewById(R.id.textView3);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        EditText email = findViewById(R.id.editTextText);
        EditText password = findViewById(R.id.editTextText2);

        TextView forgotpassword = findViewById(R.id.textView31);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Stremail = email.getText().toString().trim();
progressDialog.show();
                if (Stremail.isEmpty()) {
                    email.setError("Please enter your email");
                    email.requestFocus();
                    progressDialog.dismiss();
                    return;
                }



                FirebaseAuth.getInstance().sendPasswordResetEmail(Stremail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
progressDialog.dismiss();

                                    Toast.makeText(SignInActivity.this,
                                            "Reset email sent. Check your inbox.",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(SignInActivity.this,
                                            "Failed to send reset email. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    WarinigAlert.showCustomAlert(SignInActivity.this,"Plese Enter Email");
                }else if(password.getText().toString().isEmpty()){
                    WarinigAlert.showCustomAlert(SignInActivity.this,"Plese Enter Password");

                }else {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String strEmail = email.getText().toString().trim();
                    String strPassword = password.getText().toString().trim();
                    progressDialog.show();

                    auth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    Log.d("CliSignInActivity", "User signed in: " + user.getEmail());
                                }
                                String userID = user.getUid();
                                Log.d("CliSignInActivity", "Fetching document for userID: " + userID);


                                firestore.collection("user").document(userID)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    User loggedUser = documentSnapshot.toObject(User.class);
                                                    Long userStatus = documentSnapshot.getLong("status");
                                                    if(userStatus==1){
                                                        // Save user information in SharedPreferences
                                                        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("user_id", loggedUser.getUserId());
                                                        editor.putString("user_fname", loggedUser.getFname());
                                                        editor.putString("user_lname", loggedUser.getLname());
                                                        editor.putString("email", loggedUser.getEmail());
                                                        editor.putString("mobile", loggedUser.getMobile());
                                                        editor.putBoolean("is_logged_in", true);
                                                        editor.commit();
                                                        Log.d("CliSignInActivity", "Saved User Data: " + sharedPreferences.getAll().toString());

                                                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                                        intent.putExtra("navigateTo", "home");
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                            progressDialog.dismiss();
                                                        WarinigAlert.showCustomAlert(SignInActivity.this,"Blocked Account");
                                                    }


                                                } else {
                                                    Log.d("CliSignInActivity", "No document found for userID: " + userID);
                                                    progressDialog.dismiss();
                                                    WarinigAlert.showCustomAlert(SignInActivity.this, "No user data found. Please contact support.");
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Log.e("CliSignInActivity", "Firestore Error: " + e.getMessage());
                                                WarinigAlert.showCustomAlert(SignInActivity.this, "Try Again");
                                            }
                                        });

                            }else{
                                progressDialog.dismiss();
                                WarinigAlert.showCustomAlert(SignInActivity.this, "Error: " + task.getException().getMessage());
                            }
                        }
                    });



                }

            }
        });


    }
}