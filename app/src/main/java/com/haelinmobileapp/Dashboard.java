package com.haelinmobileapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends Fragment {

    private TextView lblUser;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get reference to the TextView
        lblUser = view.findViewById(R.id.lbl_user);

        // Set the user's name
        setUserName();

        return view;
    }

    private void  setUserName(){

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String displayName = currentUser.getDisplayName();

            lblUser.setText("Hi "+displayName);

        } else{
            lblUser.setText("Hi User");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserName();
    }
}