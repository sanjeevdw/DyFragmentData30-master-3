package com.example.android.dyfragmentdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class
NewArrivalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new NewArrivalFragment()).commit();

    }}


