package com.example.blanche.mynews.controllers.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.blanche.mynews.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get a support actionbar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
