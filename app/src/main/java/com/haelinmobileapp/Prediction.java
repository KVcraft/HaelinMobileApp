package com.haelinmobileapp;

public class Prediction {
    private String predId;
    private String diseaseType;
    private String date;
    private double score;
    private int prediction; // 1 or 0

    public Prediction() {}

    public Prediction(String predId,String diseaseType, String date, double score, int prediction) {
        this.predId = predId;
        this.diseaseType = diseaseType;
        this.date = date;
        this.score = score;
        this.prediction = prediction;
    }

    public String getPredId(){return predId;}
    public String getDiseaseType() { return diseaseType; }
    public String getDate() { return date; }
    public double getScore() { return score; }
    public int getPrediction() { return prediction; }
}

