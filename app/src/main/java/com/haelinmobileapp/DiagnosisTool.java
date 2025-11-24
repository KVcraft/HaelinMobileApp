package com.haelinmobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haelinmobileapp.retrofit.ApiService;
import com.haelinmobileapp.retrofit.ChikunRetrofitClient;
import com.haelinmobileapp.retrofit.ChikunSymptoms;
import com.haelinmobileapp.retrofit.PredReponse;
import com.haelinmobileapp.retrofit.DengueSymptoms;
import com.haelinmobileapp.retrofit.DengueRetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class DiagnosisTool extends Fragment {

    Spinner spDisease;
    LinearLayout linearFrame;
    Button btnSubmit;

     RadioButton radFever, radHeadache, radjointPains, radBleed;
     RadioButton radSex, radCold, radMyalgia, radFatigue, radVomitting;
     RadioButton radArthritis, radConjuctivitis, radNausea;
     RadioButton radMaculopapularRash, radEyePain, radChills, radSwelling;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diagnosis_tool, container, false);

        spDisease = view.findViewById(R.id.sp_disease);
        linearFrame = view.findViewById(R.id.linear_frame);

        spDisease.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {

                String selected = parent.getItemAtPosition(position).toString();
                linearFrame.removeAllViews();  // clear old views

                if (selected.equals("Dengue")) {
                    View dengueView = inflater.inflate(R.layout.frame_dengue, linearFrame, false);
                    linearFrame.addView(dengueView);

                    // find radio buttons inside dengue layout
                    radFever = dengueView.findViewById(R.id.rad_fever);
                    radHeadache = dengueView.findViewById(R.id.rad_headache);
                    radjointPains = dengueView.findViewById(R.id.rad_joint);
                    radBleed = dengueView.findViewById(R.id.rad_bleed);

                    // Clear chikungunya references
                    radCold = null;
                    radMyalgia = null;
                    radFatigue = null;
                    radVomitting = null;
                    radArthritis = null;
                    radConjuctivitis = null;
                    radNausea = null;
                    radMaculopapularRash = null;
                    radEyePain = null;
                    radChills = null;
                    radSwelling = null;

                } else if (selected.equals("Chikungunya")) {
                    View chikunView = inflater.inflate(R.layout.frame_chikun, linearFrame, false);
                    linearFrame.addView(chikunView);

                    radSex = chikunView.findViewById(R.id.rad_sex);
                    radBleed = chikunView.findViewById(R.id.rad_sex);
                    radCold = chikunView.findViewById(R.id.rad_cold);
                    radjointPains = chikunView.findViewById(R.id.rad_jointPains);
                    radMyalgia = chikunView.findViewById(R.id.rad_myalgia);
                    radFatigue = chikunView.findViewById(R.id.rad_fatigue);
                    radVomitting = chikunView.findViewById(R.id.rad_vomitting);
                    radArthritis = chikunView.findViewById(R.id.rad_arthritis);
                    radConjuctivitis = chikunView.findViewById(R.id.rad_conjuctivitis);
                    radNausea = chikunView.findViewById(R.id.rad_nausea);
                    radMaculopapularRash = chikunView.findViewById(R.id.rad_maculopapular_rash);
                    radEyePain = chikunView.findViewById(R.id.rad_eye_pain);
                    radChills = chikunView.findViewById(R.id.rad_chills);
                    radSwelling = chikunView.findViewById(R.id.rad_swelling);

                } else {
                    // Clear all references if not dengue or chikungunya
                    radSex = null;
                    radFever = null;
                    radHeadache = null;
                    radjointPains = null;
                    radBleed = null;
                    radCold = null;
                    radMyalgia = null;
                    radFatigue = null;
                    radVomitting = null;
                    radArthritis = null;
                    radConjuctivitis = null;
                    radNausea = null;
                    radMaculopapularRash = null;
                    radEyePain = null;
                    radChills = null;
                    radSwelling = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // add submit button programmatically or find if already in layout
        btnSubmit = view.findViewById(R.id.btn_submit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> sendSymptoms());
        }

        return view;
    }

    private void sendSymptoms() {
        if (radFever == null) {
            Toast.makeText(getContext(), "Please select a disease and fill symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected disease from spinner
        String selectedDisease = spDisease.getSelectedItem().toString();

        if (selectedDisease.equals("Dengue")) {
            DengueSymptoms symptoms = new DengueSymptoms(
                    radFever.isChecked() ? 1 : 0,
                    radHeadache.isChecked() ? 1 : 0,
                    radjointPains.isChecked() ? 1 : 0,
                    radBleed.isChecked() ? 1 : 0
            );

            ApiService apiService = DengueRetrofitClient.getInstance().create(ApiService.class);
            Call<PredReponse> call = apiService.sendDengueSymptoms(symptoms);

            call.enqueue(new retrofit2.Callback<PredReponse>() {
                @Override
                public void onResponse(Call<PredReponse> call, retrofit2.Response<PredReponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PredReponse predReponse = response.body();
                        displayPredictionResult(predReponse.getPrediction());
                        savePrediction(predReponse.getPrediction(), predReponse.getPred_score(), predReponse.getPred_date());
                        clearInputs();
                    }
                }

                @Override
                public void onFailure(Call<PredReponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else if (selectedDisease.equals("Chikungunya")) {
            // Create Chikungunya symptoms object
            ChikunSymptoms symptoms = new ChikunSymptoms(
                    radSex.isChecked() ? 1: 0,
                    radFever.isChecked() ? 1 : 0,
                    radCold != null && radCold.isChecked() ? 1 : 0,
                    radjointPains.isChecked() ? 1 : 0,
                    radMyalgia != null && radMyalgia.isChecked() ? 1 : 0,
                    radHeadache != null && radHeadache.isChecked() ? 1 : 0,
                    radFatigue != null && radFatigue.isChecked() ? 1 : 0,
                    radVomitting != null && radVomitting.isChecked() ? 1 : 0,
                    radArthritis != null && radArthritis.isChecked() ? 1 : 0,
                    radConjuctivitis != null && radConjuctivitis.isChecked() ? 1 : 0,
                    radNausea != null && radNausea.isChecked() ? 1 : 0,
                    radMaculopapularRash != null && radMaculopapularRash.isChecked() ? 1 : 0,
                    radEyePain != null && radEyePain.isChecked() ? 1 : 0,
                    radChills != null && radChills.isChecked() ? 1 : 0,
                    radSwelling != null && radSwelling.isChecked() ? 1 : 0
            );

            // You'll need a different Retrofit client for Chikungunya or modify your existing one
            ApiService apiService = ChikunRetrofitClient.getInstance().create(ApiService.class);
            Call<PredReponse> call = apiService.sendChikunSymptoms(symptoms);

            call.enqueue(new retrofit2.Callback<PredReponse>() {
                @Override
                public void onResponse(Call<PredReponse> call, retrofit2.Response<PredReponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PredReponse predReponse = response.body();
                        displayPredictionResult(predReponse.getPrediction());
                        savePrediction(predReponse.getPrediction(), predReponse.getPred_score(), predReponse.getPred_date());
                        clearInputs();
                    }
                }

                @Override
                public void onFailure(Call<PredReponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please select a valid disease", Toast.LENGTH_SHORT).show();
        }
    }

    // Save prediction to Firestore
    private void savePrediction(int prediction, float pred_score, String pred_date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in, can't save prediction", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String selectedDisease = spDisease.getSelectedItem().toString();

        Map<String, Object> predictionData = new HashMap<>();
        predictionData.put("prediction", prediction);
        predictionData.put("pred_score", pred_score);
        predictionData.put("pred_date", pred_date);
        predictionData.put("disease_type", selectedDisease); // Save which disease was predicted

        db.collection("users")
                .document(userId)
                .collection("predictions")
                .add(predictionData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Prediction saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save prediction", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayPredictionResult(int prediction) {
        TextView txtPred = getView().findViewById(R.id.txt_pred);
        String selectedDisease = spDisease.getSelectedItem().toString();

        String resultText;
        if (prediction == 1) {
            resultText = selectedDisease + " Positive";
        } else if (prediction == 0) {
            resultText = selectedDisease + " Negative";
        } else {
            resultText = "Unknown Result";
        }

        txtPred.setText(resultText);
    }

    private void clearInputs() {
        // Clear common symptoms
        if (radFever != null) radFever.setChecked(false);
        if (radHeadache != null) radHeadache.setChecked(false);
        if (radjointPains != null) radjointPains.setChecked(false);
        if (radBleed != null) radBleed.setChecked(false);

        // Clear Chikungunya specific symptoms
        if (radSex != null) radSex.setChecked(false);
        if (radCold != null) radCold.setChecked(false);
        if (radMyalgia != null) radMyalgia.setChecked(false);
        if (radFatigue != null) radFatigue.setChecked(false);
        if (radVomitting != null) radVomitting.setChecked(false);
        if (radArthritis != null) radArthritis.setChecked(false);
        if (radConjuctivitis != null) radConjuctivitis.setChecked(false);
        if (radNausea != null) radNausea.setChecked(false);
        if (radMaculopapularRash != null) radMaculopapularRash.setChecked(false);
        if (radEyePain != null) radEyePain.setChecked(false);
        if (radChills != null) radChills.setChecked(false);
        if (radSwelling != null) radSwelling.setChecked(false);
    }

}
