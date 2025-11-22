package com.haelinmobileapp;

public class UserMessage {
    private String message;

    public UserMessage(String message) {
        this.message = message;
    }

    // Getter (Retrofit uses this to serialize JSON)
    public String getMessage() {
        return message;
    }

    // Setter (optional, but good to have)
    public void setMessage(String message) {
        this.message = message;
    }
}

