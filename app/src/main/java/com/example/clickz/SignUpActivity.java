package com.example.clickz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import model.User;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText fname = findViewById(R.id.editTextText3);
        EditText lname = findViewById(R.id.editTextText4);
        EditText email = findViewById(R.id.editTextText5);
        EditText mobile = findViewById(R.id.editTextText6);
        EditText password = findViewById(R.id.editTextText7);
        Button b3 = findViewById(R.id.button3);
        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String strEmail = email.getText().toString().trim();
                String strMobile = mobile.getText().toString().trim();
                String strPasswordText = password.getText().toString().trim();

                if (firstName.isEmpty()) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter Your First Name");
                } else if (lastName.isEmpty()) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter Your Last Name");
                } else if (strEmail.isEmpty()) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter Your Email");
                } else if (strMobile.isEmpty()) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter Your Mobile Number");
                } else if (!strMobile.matches("^(?:07[01245678]\\d{7}|\\+94[71245678]\\d{8})$")) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter A Valid Mobile Number");
                } else if (strPasswordText.isEmpty()) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Please Enter Password");
                } else if (!strPasswordText.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
                    WarinigAlert.showCustomAlert(SignUpActivity.this, "Add a Strong Password");
                } else {

                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    progressDialog.show();

                    firebaseFirestore.collection("user")
                            .whereEqualTo("email", strEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {

                                            progressDialog.dismiss();
                                            WarinigAlert.showCustomAlert(SignUpActivity.this, "Email Already Exists");
                                        } else {
                                            auth.createUserWithEmailAndPassword(strEmail, strPasswordText)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseUser user = auth.getCurrentUser();
                                                                String userId = user.getUid();
                                                                int status = 1;

                                                                User newUser = new User(userId, firstName, lastName, strEmail, strMobile,status);


                                                                firebaseFirestore.collection("user")
                                                                        .document(userId)
                                                                        .set(newUser)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                progressDialog.show();
                                                                                SuccessAlert.showSuccessAlert(SignUpActivity.this, "Account Created");
                                                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                                startActivity(intent);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                progressDialog.dismiss();
                                                                                WarinigAlert.showCustomAlert(SignUpActivity.this, "Try Again");
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        WarinigAlert.showCustomAlert(SignUpActivity.this, "Something Went Wrong");
                                    }
                                }
                            });
                }
            }
        });


    }

}