package com.haelinmobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.ViewHolder> {

    private ArrayList<Prediction> list;

    public PredictionAdapter(ArrayList<Prediction> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_his_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prediction p = list.get(position);

        holder.medDisease.setText("Disease: "+p.getDiseaseType());
        holder.medDate.setText(p.getDate());
        holder.medScore.setText("Score: " + p.getScore());
        holder.medRisk.setText(p.getPrediction() == 1 ? "Positive" : "Negative");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medDisease, medDate, medRisk, medScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medDisease = itemView.findViewById(R.id.med_disease);
            medDate = itemView.findViewById(R.id.med_date);
            medRisk = itemView.findViewById(R.id.med_risk);
            medScore = itemView.findViewById(R.id.med_score);
        }
    }
}

