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
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.utils.GetArticlesWorker;
import com.example.blanche.mynews.models.SearchArticles.SearchArticle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {


    @BindView(R.id.search_button) Button button;
    @BindView(R.id.spinner_button_start_date) Button beginDateButton;
    @BindView(R.id.spinner_button_end_date) Button endDateButton;
    @BindView(R.id.edit_search) EditText editText;
    @BindView(R.id.checkboxArts) CheckBox checkboxArts;
    @BindView(R.id.checkboxPolitics) CheckBox checkboxPolitics;
    @BindView(R.id.checkboxBusiness) CheckBox checkboxBusiness;
    @BindView(R.id.checkboxSports) CheckBox checkboxSports;
    @BindView(R.id.checkboxEntrepreneurs) CheckBox checkboxEntrepreneurs;
    @BindView(R.id.checkboxTravel) CheckBox checkboxTravel;
    @BindView(R.id.layout_dates) LinearLayout layoutDates;
    @BindView(R.id.layout_spinners) LinearLayout layoutSpinners;
    @BindView(R.id.switch_button) Switch switchButton;
    @BindView(R.id.surface_view) SurfaceView surfaceView;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String KEY_ACTIVITY = "key_activity";
    public static final String CATEGORIES_SEARCH = "categories";
    public static final String CATEGORIES_NOTIFICATION = "categories_notif";
    public static final String KEYWORD_SEARCH = "keyword";
    public static final String KEYWORD_NOTIFICATION = "keyword_notif";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    private DatePickerDialog datePickerDialog;
    private String currentDate;
    ActionBar actionBar;
    SharedPreferences preferences;


    public static final String SWITCH_BUTTON_STATE = "state";
    OneTimeWorkRequest request;
    PeriodicWorkRequest periodicRequest;
    Data data;
    Boolean isMethodDoingThis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        configureToolbar();
        displayNotificationOrSearchScreen();
        setCurrentKeyword();
        configureDatesButtons();
    }

    //----------------
    //CONFIGURATION
    //----------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get a support actionbar corresponding to this toolbar
        actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //SEARCH ACTIVITY
    //---------------------
    private void configureDatesButtons() {
        currentDate = getCurrentDate();
        setDateOnButton(beginDateButton, currentDate);
        setDateOnButton(endDateButton, currentDate);
    }

    //NOTIFICATION ACTIVITY
    //-----------------------------
    private void configureSwitchButton() {

        System.out.println("WE ENTER HERE FIRST");

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("print bool " + isMethodDoingThis);
               // if (!isMethodDoingThis) {
                   // if (!paramsAreMissing()) {

                        if (isChecked) {
                            preferences.edit().putString(KEYWORD_NOTIFICATION, editText.getText().toString()).apply();
                            preferences.edit().putInt(SWITCH_BUTTON_STATE, 0).apply();
                            getCheckedCheckboxes();
                            configureWorkRequest();
                            //WorkManager.getInstance().enqueue(request);
                            WorkManager.getInstance().enqueueUniquePeriodicWork("periodic_work", ExistingPeriodicWorkPolicy.REPLACE, periodicRequest);

                        } else {
                            //uncheck the boxes, erase the edit text
                            editText.setText(null);
                            preferences.edit().putInt(SWITCH_BUTTON_STATE, 1).apply();
                            System.out.println("here we print the button state = " + preferences.getInt(SWITCH_BUTTON_STATE, -1));
                            preferences.edit().putString(KEYWORD_NOTIFICATION, null).apply();
                            preferences.edit().putString(CATEGORIES_NOTIFICATION, null).apply();
                        }
                    }
               // }
          //  }
        });
    }

    private void configureWorkRequest() {
        data = new Data.Builder()
                .putString(GetArticlesWorker.CATEGORIES_WORKER, preferences.getString(CATEGORIES_NOTIFICATION, null))
                .putString(GetArticlesWorker.KEYWORD_WORKER, preferences.getString(KEYWORD_NOTIFICATION, null))
                .build();

        //request = new OneTimeWorkRequest.Builder(GetArticlesWorker.class)
        //        .setInputData(data)
        //        .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                //.setRequiresDeviceIdle(true)
                .build();

        periodicRequest = new PeriodicWorkRequest.Builder(GetArticlesWorker.class, 25, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();


    }


    //---------------
    //ACTIONS
    //---------------

    //SEARCH ACTIVITY
    //--------------------------------
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
            preferences.edit().putString(KEYWORD_SEARCH, editText.getText().toString()).apply();
            launchSearchArticlesActivity();
        }
    }

    @OnClick({R.id.spinner_button_start_date, R.id.spinner_button_end_date})
    public void chooseDates(View view) {
        createDatePickerDialog(view);
        datePickerDialog.show();
    }

    //NOTIFICATIONS ACTIVITY
    //-------------------------------------



    //------------------------------------------------------------

    //FOR SEARCH ACTIVITY
    //--------------------------------

    private void createDatePickerDialog(final View v) {
       final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

               int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
               int currentMonth = calendar.get(Calendar.MONTH)+1;
               int currentYear = calendar.get(Calendar.YEAR);
                //check if selected date is passed or not
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

    private void launchSearchArticlesActivity() {
        Intent searchArticlesActivity = new Intent(this, SearchArticlesActivity.class);
        startActivity(searchArticlesActivity);
    }

    //FOR NOTIFICATIONS
    //------------------------------------
    private void displaySwitchButtonState() {
        isMethodDoingThis = true;
        System.out.println("display swith button state" + isMethodDoingThis);
        if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
            System.out.println("we enter in here if the button state is 0");
            switchButton.setChecked(true);
        } else {
            System.out.println("or maybe we enter here??");
            System.out.println("print button state " + preferences.getInt(SWITCH_BUTTON_STATE, -1));
            switchButton.setChecked(false);
        }
        isMethodDoingThis = false;
        System.out.println("state button == " + isMethodDoingThis);
    }

    private boolean paramsAreMissing() {
        if (TextUtils.isEmpty(editText.getText().toString()) && !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked() || TextUtils.isEmpty(editText.getText().toString()) ||
                !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                        && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked()) {

            System.out.println("WE ENTER HERE");

            Toast.makeText(getApplicationContext(), "You have to enter at least one keyword and check one category!", Toast.LENGTH_SHORT).show();
            switchButton.setChecked(false);
            //preferences.edit().putString(SWITCH_BUTTON_STATE, 1).apply();

        }
        return true;

    }

    //FOR BOTH
    //---------------------------------

    public void getCheckedCheckboxes() {
        StringBuilder stringBuilder = new StringBuilder();
        if (checkboxArts.isChecked()) {
            stringBuilder.append(" " + '"' + "arts" + '"');
        }
        if (checkboxPolitics.isChecked()) {
            stringBuilder.append(" " + '"' + "politics" + '"');
        }
        if (checkboxBusiness.isChecked()) {
            stringBuilder.append(" " + '"' + "business" + '"');
        }
        if (checkboxSports.isChecked()) {
            stringBuilder.append(" " + '"' + "sports" + '"');
        }
        if (checkboxEntrepreneurs.isChecked()) {
            stringBuilder.append(" " + '"' + "entrepreneurs" + '"');
        }
        if (checkboxTravel.isChecked()) {
            stringBuilder.append(" " + '"' + "travel" + '"');
        }
        String categories = stringBuilder.toString();
        //if it s search activity we save it in search preferences
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            preferences.edit().putString(CATEGORIES_SEARCH, categories).apply();
        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {

            //if it s notification activity we save it in notifications preferences
            preferences.edit().putString(CATEGORIES_NOTIFICATION, categories).apply();
        }
    }

    private void setPreferencesToNull() {

    }

    private void setCurrentKeyword() {
        if (preferences.getString(KEYWORD_SEARCH, null) != null && preferences.getInt(KEY_ACTIVITY, -1) != 1) {
            editText.setText(preferences.getString(KEYWORD_SEARCH, null));
        } else if(preferences.getString(KEYWORD_NOTIFICATION, null) != null && preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            editText.setText(preferences.getString(KEYWORD_NOTIFICATION, null));
        }
    }

    private void displayNotificationOrSearchScreen() {
        if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            //display notification
            layoutDates.setVisibility(View.GONE);
            layoutSpinners.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            actionBar.setTitle("Notifications");
            configureSwitchButton();

        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            switchButton.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
        }
    }


}
