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
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;

public class Dashboard extends Fragment {

    private TextView lblUser, txtTips;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadUserData(user.getUid());
        }

        // Get reference to the TextView
        lblUser = view.findViewById(R.id.lbl_user);
        txtTips = view.findViewById(R.id.txt_tips);

        //Set the random tips
        String[] tips = getResources().getStringArray(R.array.health_tips);
        int randomIndex = new java.util.Random().nextInt(tips.length);
        txtTips.setText(tips[randomIndex]);



        CardView openMap = view.findViewById(R.id.btnMap);
        CardView openDiagnosis = view.findViewById(R.id.btnDiagnosis);
        CardView openMedHis = view.findViewById(R.id.btnMedHis);


        openMap.setOnClickListener(v -> startMap());
        openDiagnosis.setOnClickListener(v -> startDiagnosis());
        openMedHis.setOnClickListener(v -> startMedHis());

        return view;

    }



    @Override
    public void onStart() {
        super.onStart();
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

    public void startMedHis() {
        Fragment medHisFragment = new MedHistory();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, medHisFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        lblUser.setText("Hi "+ name);
                    }
                });
    }


}