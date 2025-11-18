package com.haelinmobileapp.retrofit;

public class LoginRequest {
    private String idToken;

    public LoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}


