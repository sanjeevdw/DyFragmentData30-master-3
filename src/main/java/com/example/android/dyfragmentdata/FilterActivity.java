package com.example.android.dyfragmentdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FilterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_category.xml layout file
        setContentView(R.layout.activity_filter);
    }
}
