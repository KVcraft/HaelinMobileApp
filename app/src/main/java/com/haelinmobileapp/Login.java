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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.RequestBody;

import java.io.IOException;

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
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"idToken\":\"" + idToken + "\"}";

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/haelin-app/user/login/patient")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Login.this, "Server not reachable", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Dash.class));
                    } else {
                        Toast.makeText(Login.this, "Backend Login Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
