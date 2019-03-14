package com.example.blanche.mynews.controllers.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blanche.mynews.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {


    @BindView(R.id.search_button) Button button;
    @BindView(R.id.edit_search) EditText editText;
    @BindView(R.id.checkbox1) CheckBox checkbox1;
    @BindView(R.id.checkbox2) CheckBox checkbox2;
    @BindView(R.id.checkbox3) CheckBox checkbox3;
    @BindView(R.id.checkbox4) CheckBox checkbox4;
    @BindView(R.id.checkbox5) CheckBox checkbox5;
    @BindView(R.id.checkbox6) CheckBox checkbox6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
        ButterKnife.bind(this);
    }

    //----------------
    //CONFIGURATION
    //----------------

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get a support actionbar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //---------------
    //ACTIONS
    //---------------

    @OnClick(R.id.search_button)
    public void submit(View view) {
        if(!checkbox1.isChecked() && !checkbox2.isChecked() && !checkbox3.isChecked()
                && !checkbox4.isChecked() && !checkbox5.isChecked() && !checkbox6.isChecked()&& TextUtils.isEmpty(editText.getText().toString())) {
            Toast.makeText(this, R.string.toast_text_no_keyword_no_checked_category, Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(editText.getText().toString())){
            Toast.makeText(this, R.string.toast_text_missing_keyword, Toast.LENGTH_SHORT).show();
        } else  if (!checkbox1.isChecked() && !checkbox2.isChecked() && !checkbox3.isChecked() && !checkbox4.isChecked() && !checkbox5.isChecked() && !checkbox6.isChecked()) {
            Toast.makeText(this, R.string.toast_text_checked_category_missing, Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(this, "Not implemented yet...", Toast.LENGTH_SHORT).show();
    }
}
