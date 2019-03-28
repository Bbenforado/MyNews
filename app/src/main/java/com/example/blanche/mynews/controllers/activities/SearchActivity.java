package com.example.blanche.mynews.controllers.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {


    @BindView(R.id.search_button)
    Button button;
    @BindView(R.id.spinner_button_start_date)
    Button beginDateButton;
    @BindView(R.id.spinner_button_end_date)
    Button endDateButton;
    @BindView(R.id.edit_search)
    EditText editText;
    @BindView(R.id.checkboxArts)
    CheckBox checkboxArts;
    @BindView(R.id.checkboxPolitics)
    CheckBox checkboxPolitics;
    @BindView(R.id.checkboxBusiness)
    CheckBox checkboxBusiness;
    @BindView(R.id.checkboxSports)
    CheckBox checkboxSports;
    @BindView(R.id.checkboxEntrepreneurs)
    CheckBox checkboxEntrepreneurs;
    @BindView(R.id.checkboxTravel)
    CheckBox checkboxTravel;

    public static final String KEYWORD = "keyword";
    public static final String ARTS = "arts";
    public static final String POLITICS = "politics";
    public static final String BUSINESS = "business";
    public static final String SPORTS = "sports";
    public static final String ENTREPRENEURS = "entrepreneurs";
    public static final String TRAVEL = "travel";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    private DatePickerDialog datePickerDialog;
    private String currentDate;
    private int day;
    private int month;
    private int year;
    SharedPreferences preferences;
    public static final String APP_PREFERENCES = "appPreferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        configureToolbar();
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        setCurrentKeyword();
        configureButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //setPreferencesToNull();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //setPreferencesToNull();
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

    private void configureButtons() {
        currentDate = getCurrentDate();
        setDateOnButton(beginDateButton, currentDate);
        setDateOnButton(endDateButton, currentDate);
    }

    //---------------
    //ACTIONS
    //---------------
    @OnClick(R.id.search_button)
    public void submit(View view) {
        if (!checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked() && TextUtils.isEmpty(editText.getText().toString())) {
            displayToastMessageWhenParamAreMissing(1);
        } else if (TextUtils.isEmpty(editText.getText().toString())) {
            displayToastMessageWhenParamAreMissing(2);
        } else if (!checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked() && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked()) {
            displayToastMessageWhenParamAreMissing(3);
        } else {
            //launch activity that displays a list of articles depending on the keywords, dates and category checked
            //save the categories selected
            getCheckedCheckboxes();
            //save the keywords selected
            preferences.edit().putString(KEYWORD, editText.getText().toString()).apply();
            launchSearchArticlesActivity();
        }
    }

    @OnClick({R.id.spinner_button_start_date, R.id.spinner_button_end_date})
    public void chooseDates(View view) {
        createDatePickerDialog(view);
        datePickerDialog.show();
    }

    private void createDatePickerDialog(final View v) {
       final Calendar calendar = Calendar.getInstance();
       day = calendar.get(Calendar.DAY_OF_MONTH);
       month = calendar.get(Calendar.MONTH);
       year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //check if selected date is passed or not
               int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
               int currentMonth = calendar.get(Calendar.MONTH)+1;
               int currentYear = calendar.get(Calendar.YEAR);

                if(year <= currentYear) {
                    if(month <= currentMonth) {
                        if (dayOfMonth <= currentDay) {
                            String strMonth = addZeroToDate(Integer.toString(month+1));
                            String strDay = addZeroToDate(Integer.toString(dayOfMonth));
                            String strYear = Integer.toString(year);
                            switch (v.getId()) {
                                case R.id.spinner_button_start_date:
                                    beginDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                                    preferences.edit().putString(BEGIN_DATE, strYear + strMonth + strDay).apply();
                                    break;
                                case R.id.spinner_button_end_date:

                                    //verifier que la begindate est avant la enddate

                                    endDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                                    preferences.edit().putString(END_DATE, strYear + strMonth + strDay).apply();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            displayWrongDateSelectedMessage(v);
                        }
                    } else {
                        displayWrongDateSelectedMessage(v);
                    }
                } else {
                    displayWrongDateSelectedMessage(v);
                }
            }
        }, year, month, day);
    }

    private void displayWrongDateSelectedMessage(View v) {
        Toast.makeText(getApplicationContext(), "You have to select a passed or current date...", Toast.LENGTH_SHORT).show();
        setDateOnButton((Button)v , currentDate);
    }

    public String addZeroToDate(String string) {
        if (string.length() == 1) {
            string = "0" + string;
        }
        return string;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }

    public void getCheckedCheckboxes() {
        if (checkboxArts.isChecked()) {
            preferences.edit().putString(ARTS, "arts").apply();
        } else {
            preferences.edit().putString(ARTS, null).apply();
        }
        if (checkboxPolitics.isChecked()) {
            preferences.edit().putString(POLITICS, "politics").apply();
        }else {
            preferences.edit().putString(POLITICS, null).apply();
        }
        if (checkboxBusiness.isChecked()) {
            preferences.edit().putString(BUSINESS, "business").apply();
        }else {
            preferences.edit().putString(BUSINESS, null).apply();
        }
        if (checkboxSports.isChecked()) {
            preferences.edit().putString(SPORTS, "sports").apply();
        }else {
            preferences.edit().putString(SPORTS, null).apply();
        }
        if (checkboxEntrepreneurs.isChecked()) {
            preferences.edit().putString(ENTREPRENEURS, "entrepreneurs").apply();
        }else {
            preferences.edit().putString(ENTREPRENEURS, null).apply();
        }
        if (checkboxTravel.isChecked()) {
            preferences.edit().putString(TRAVEL, "travel").apply();
        } else {
                preferences.edit().putString(TRAVEL, null).apply();
            }
        }

    public void setDateOnButton(Button button, String date) {
        button.setText(date);
    }

    private void displayToastMessageWhenParamAreMissing(int key) {
        switch (key) {
            case 1:
                Toast.makeText(this, R.string.toast_text_no_keyword_no_checked_category, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, R.string.toast_text_missing_keyword, Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, R.string.toast_text_checked_category_missing, Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;
        }
    }

    private void setPreferencesToNull() {
        preferences.edit().putString(KEYWORD, null).apply();
        preferences.edit().putString(ARTS, null).apply();
        preferences.edit().putString(POLITICS, null).apply();
        preferences.edit().putString(BUSINESS, null).apply();
        preferences.edit().putString(SPORTS, null).apply();
        preferences.edit().putString(ENTREPRENEURS, null).apply();
        preferences.edit().putString(TRAVEL, null).apply();
        preferences.edit().putString(BEGIN_DATE, null).apply();
        preferences.edit().putString(END_DATE, null).apply();
    }

    private void launchSearchArticlesActivity() {
        Intent searchArticlesActivity = new Intent(this, SearchArticlesActivity.class);
        startActivity(searchArticlesActivity);
    }

    private void setCurrentKeyword() {
        if (preferences.getString(KEYWORD, null) != null) {
            editText.setText(preferences.getString(KEYWORD, null));
        }
    }



}
