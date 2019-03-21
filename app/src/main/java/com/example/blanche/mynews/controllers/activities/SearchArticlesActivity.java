package com.example.blanche.mynews.controllers.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.blanche.mynews.R;

public class SearchArticlesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_articles);

        configureRecyclerView();
    }

    //----------------------------
    //CONFIGURATION
    //----------------------------
    private void configureRecyclerView() {

    }
}
