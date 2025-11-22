package com.haelinmobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class DiagnosisTool extends Fragment {

    Spinner spDisease;
    LinearLayout linearFrame;

    public DiagnosisTool() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagnosis_tool, container, false);

        // find views *from the fragment view, not the activity*
        spDisease = view.findViewById(R.id.sp_disease);
        linearFrame = view.findViewById(R.id.linear_frame);

        spDisease.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                linearFrame.removeAllViews();

                if (selected.equals("Dengue")) {
                    View dengueView = getLayoutInflater().inflate(R.layout.frame_dengue, linearFrame, false);
                    linearFrame.addView(dengueView);
                }

                // add more conditions here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }
}
