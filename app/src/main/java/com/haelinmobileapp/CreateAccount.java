package com.haelinmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haelinmobileapp.R;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private static final String TAG = "CreateAccount";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // UI elements
    private TextInputEditText nameEditText, nicEditText, cityEditText, contactEditText;
    private TextInputEditText emailEditText, passEditText, conPassEditText;
    private Button btnCreateAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account); // Make sure this matches your XML file name

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        initializeViews();

        // Set click listener
        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.name);
        nicEditText = findViewById(R.id.nic);
        cityEditText = findViewById(R.id.city);
        contactEditText = findViewById(R.id.contact);
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        conPassEditText = findViewById(R.id.conPass);
        btnCreateAcc = findViewById(R.id.btnCreateAcc);
    }

    public void createAccount() {
        // Get input values
        String name = nameEditText.getText().toString().trim();
        String nic = nicEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String confirmPassword = conPassEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Full Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nic)) {
            nicEditText.setError("NIC is required");
            nicEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            cityEditText.setError("City is required");
            cityEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            contactEditText.setError("Contact is required");
            contactEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passEditText.setError("Password is required");
            passEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passEditText.setError("Password must be at least 6 characters");
            passEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            conPassEditText.setError("Please confirm your password");
            conPassEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            conPassEditText.setError("Passwords do not match");
            conPassEditText.requestFocus();
            return;
        }

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Save additional user data to Firestore
                            saveUserToFirestore(user.getUid(), name, nic, city, contact, email);

                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String nic, String city,
                                     String contact, String email) {
        // Create a new user document
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("nic", nic);
        user.put("city", city);
        user.put("contact", contact);
        user.put("email", email);
        user.put("userId", userId);
        user.put("role", new String("PATIENT"));
        user.put("createdAt", com.google.firebase.Timestamp.now());

        // Add document to Firestore
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved to Firestore");
                            Toast.makeText(CreateAccount.this,
                                    "Account created successfully!", Toast.LENGTH_SHORT).show();

                             startActivity(new Intent(CreateAccount.this, Login.class));
                             finish();

                        } else {
                            Log.w(TAG, "Error saving user data", task.getException());
                            Toast.makeText(CreateAccount.this,
                                    "Failed to save user data: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void clearForm() {
        nameEditText.setText("");
        nicEditText.setText("");
        cityEditText.setText("");
        contactEditText.setText("");
        emailEditText.setText("");
        passEditText.setText("");
        conPassEditText.setText("");
    }

    public void backLogin (View view){
        startActivity(new Intent(this, Login.class));
    }
}