package com.haelinmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.haelinmobileapp.retrofit.ApiService;
import com.haelinmobileapp.retrofit.LoginRequest;
import com.haelinmobileapp.retrofit.LoginResponse;
import com.haelinmobileapp.retrofit.RetrofitClient;

import retrofit2.Call;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText emailField, passwordField;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.txt_email);
        passwordField = findViewById(R.id.txt_password);
        loginBtn = findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnCompleteListener(idTokenTask -> {
                                if (idTokenTask.isSuccessful()) {
                                    String idToken = idTokenTask.getResult().getToken();
                                    callBackend(idToken);
                                } else {
                                    Toast.makeText(this, "Token error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Firebase Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callBackend(String idToken) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        LoginRequest request = new LoginRequest(idToken);

        Call<LoginResponse> call = api.loginPatient("Bearer " + idToken, request);

        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   retrofit2.Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Login.this, Dash.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "Backend Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this, "Server not reachable", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
