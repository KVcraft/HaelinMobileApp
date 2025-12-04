package com.haelinmobileapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class MedHistory extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private TextView emptyStateText;
    private ArrayList<Prediction> originalList;
    private ArrayList<Prediction> filteredList;
    private PredictionAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_history, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = view.findViewById(R.id.med_history_view);
        searchBar = view.findViewById(R.id.med_search);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists
        originalList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new PredictionAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        setupSearch();

        // Load predictions
        loadPredictions();

        return view;
    }

    private void setupSearch() {
        // Text change listener for live filtering
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Live filtering as user types
                filterPredictions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void filterPredictions(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            // If search query is empty, show all items
            filteredList.addAll(originalList);
        } else {
            // Convert query to lowercase for case-insensitive search
            String lowerCaseQuery = query.toLowerCase().trim();

            // Filter items based on search query
            for (Prediction prediction : originalList) {
                // Search in disease type, date, score, and risk status
                if (prediction.getDiseaseType().toLowerCase().contains(lowerCaseQuery) ||
                        prediction.getDate().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(prediction.getScore()).contains(query) ||
                        (prediction.getPrediction() == 1 ? "positive" : "negative").contains(lowerCaseQuery)) {
                    filteredList.add(prediction);
                }
            }
        }

        // Update UI based on filtered results
        adapter.notifyDataSetChanged();
    }



    private void loadPredictions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .collection("predictions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        originalList.add(new Prediction(
                                doc.getId(),
                                doc.getString("disease_type"),
                                doc.getString("pred_date"),
                                doc.getDouble("pred_score"),
                                doc.getLong("prediction").intValue()
                        ));
                    }

                    // Initialize filtered list with all items
                    filteredList.clear();
                    filteredList.addAll(originalList);

                    // Update UI
                    adapter.notifyDataSetChanged();

                    // If there's existing search query, apply it
                    if (searchBar != null && searchBar.getText().length() > 0) {
                        filterPredictions(searchBar.getText().toString());
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    if (emptyStateText != null) {
                        emptyStateText.setText("Failed to load predictions");
                        emptyStateText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }
}