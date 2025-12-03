package com.haelinmobileapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class MedHistory extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Prediction> list;
    private PredictionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_med_history, container, false);

        recyclerView = view.findViewById(R.id.med_history_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new PredictionAdapter(list);
        recyclerView.setAdapter(adapter);

        loadPredictions();

        return view;

    }

    private void loadPredictions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("predictions")
                .get()
                .addOnSuccessListener(q -> {
                    list.clear();

                    for (DocumentSnapshot doc : q.getDocuments()) {
                        list.add(new Prediction(
                                doc.getId(),
                                doc.getString("disease_type"),
                                doc.getString("pred_date"),
                                doc.getDouble("pred_score"),
                                doc.getLong("prediction").intValue()
                        ));
                    }

                    adapter.notifyDataSetChanged();
                });
    }

}




