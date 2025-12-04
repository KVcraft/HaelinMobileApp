package com.haelinmobileapp.retrofit;

public class ChikunSymptoms {
    private int sex;
    private int fever;
    private int cold;
    private int joint_pains;
    private int myalgia;
    private int headache;
    private int fatigue;
    private int vomitting;
    private int arthritis;
    private int Conjuctivitis;
    private int Nausea;
    private int Maculopapular_rash;
    private int Eye_Pain;
    private int Chills;
    private int Swelling;

    // Constructors
    public ChikunSymptoms() {
    }

    public ChikunSymptoms(int sex, int fever, int cold, int joint_pains, int myalgia,
                          int headache, int fatigue, int vomitting, int arthritis,
                          int conjuctivitis, int nausea, int maculopapularRash,
                          int Eye_Pain, int chills, int swelling) {
        this.sex = sex;
        this.fever = fever;
        this.cold = cold;
        this.joint_pains = joint_pains;
        this.myalgia = myalgia;
        this.headache = headache;
        this.fatigue = fatigue;
        this.vomitting = vomitting;
        this.arthritis = arthritis;
        this.Conjuctivitis = conjuctivitis;
        this.Nausea = nausea;
        this.Maculopapular_rash = maculopapularRash;
        this.Eye_Pain = Eye_Pain;
        this.Chills = chills;
        this.Swelling = swelling;
    }
}