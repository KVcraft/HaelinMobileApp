package com.haelinmobileapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.config.Configuration;

public class Dashboard extends Fragment {

    private TextView lblUser;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Load config + set user agent BEFORE inflating
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );

        Configuration.getInstance().setUserAgentValue(
                requireContext().getPackageName()
        );

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get reference to the TextView
        lblUser = view.findViewById(R.id.lbl_user);

        // Set the user's name
        setUserName();

        CardView openMap = view.findViewById(R.id.btnMap);
        CardView openDiagnosis = view.findViewById(R.id.btnDiagnosis);

        openMap.setOnClickListener(v -> startMap());
        openDiagnosis.setOnClickListener(v -> startDiagnosis());

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

    public void startMap() {
        Fragment mapFragment = new DocMap();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, mapFragment)
                .addToBackStack(null)
                .commit();
    }

    public void startDiagnosis() {
        Fragment diagnosisFragment = new DiagnosisTool();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, diagnosisFragment)
                .addToBackStack(null)
                .commit();
    }

}