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
import com.haelinmobileapp.retrofit.DenguePredReponse;
import com.haelinmobileapp.retrofit.DengueSymptoms;
import com.haelinmobileapp.retrofit.DengueRetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class DiagnosisTool extends Fragment {

    Spinner spDisease;
    LinearLayout linearFrame;
    Button btnSubmit;

    // radio buttons for dengue symptoms
    RadioButton radFever, radHeadache, radJoint, radBleed;

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
                    radJoint = dengueView.findViewById(R.id.rad_joint);
                    radBleed = dengueView.findViewById(R.id.rad_bleed);
                } else {
                    // Clear references if not dengue
                    radFever = null;
                    radHeadache = null;
                    radJoint = null;
                    radBleed = null;
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

        DengueSymptoms symptoms = new DengueSymptoms(
                radFever.isChecked() ? 1 : 0,
                radHeadache.isChecked() ? 1 : 0,
                radJoint.isChecked() ? 1 : 0,
                radBleed.isChecked() ? 1 : 0
        );

        ApiService apiService = DengueRetrofitClient.getInstance().create(ApiService.class);

        Call<DenguePredReponse> call = apiService.sendDengueSymptoms(symptoms);

        call.enqueue(new retrofit2.Callback<DenguePredReponse>() {
            @Override
            public void onResponse(Call<DenguePredReponse> call, retrofit2.Response<DenguePredReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DenguePredReponse denguePredReponse = response.body();

                    displayPredictionResult(denguePredReponse.getPrediction());

                    savePrediction(denguePredReponse.getPrediction(), denguePredReponse.getPred_score(), denguePredReponse.getPred_date());

                    clearInputs();

                }

            }

            @Override
            public void onFailure(Call<DenguePredReponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        Map<String, Object> predictionData = new HashMap<>();
        predictionData.put("prediction", prediction);
        predictionData.put("pred_score", pred_score);
        predictionData.put("pred_date", pred_date);

        db.collection("users")
                .document(userId)
                .collection("predictions")
                .add(predictionData);
    }

    private void displayPredictionResult(int prediction) {
        TextView txtPred = getView().findViewById(R.id.txt_pred);

        if (prediction == 1) {
            txtPred.setText("Positive");
        } else if (prediction == 0) {
            txtPred.setText("Negative");
        } else {
            txtPred.setText("Unknown");
        }
    }


    private void clearInputs() {

        radFever.setChecked(false);
        radHeadache.setChecked(false);
        radJoint.setChecked(false);
        radBleed.setChecked(false);
    }

}
