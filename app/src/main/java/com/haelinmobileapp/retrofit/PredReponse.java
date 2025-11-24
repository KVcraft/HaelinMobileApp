package com.haelinmobileapp.retrofit;

public class PredReponse {

    private int prediction;    // 0 or 1
    private float pred_score;  // confidence score
    private String pred_date;  // timestamp string

    // Getters (and setters if you want)
    public int getPrediction() { return prediction; }
    public float getPred_score() { return pred_score; }
    public String getPred_date() { return pred_date; }
}
