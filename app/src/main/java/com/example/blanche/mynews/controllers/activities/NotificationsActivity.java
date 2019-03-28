package com.example.blanche.mynews.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.utils.MyAlarmReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends AppCompatActivity {

    private static final String KEYWORD_NOTIFICATION = "keyword";
    private static final String ARTS_NOTIFICATION = "arts";
    private static final String POLITICS_NOTIFICATION = "politics";
    public static final String BUSINESS_NOTIFICATION = "business";
    public static final String SPORTS_NOTIFICATION = "sports";
    public static final String ENTREPRENEURS_NOTIFICATION = "entrepreneurs";
    public static final String TRAVEL_NOTIFICATION = "travel";
    @BindView(R.id.switch_button) Switch switchButton;
    @BindView(R.id.edit_search) EditText editText;
    @BindView(R.id.checkboxArts) CheckBox checkboxArts;
    @BindView(R.id.checkboxPolitics) CheckBox checkboxPolitics;
    @BindView(R.id.checkboxBusiness) CheckBox checkboxBusiness;
    @BindView(R.id.checkboxSports) CheckBox checkboxSports;
    @BindView(R.id.checkboxEntrepreneurs) CheckBox checkboxEntrepreneurs;
    @BindView(R.id.checkboxTravel) CheckBox checkboxTravel;
    private PendingIntent pendingIntent;
    private SharedPreferences preferences;
    public static final String APP_PREFERENCES = "appPreferences";

    public static final String SWITCH_BUTTON_STATE = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        displayLastKeyword();

        configureToolbar();
        configureSwitchButton();
        displayLastSwitchButtonState();
        configureAlarmManager();
        setPreferencesToNull();
    }


    //----------------------------
    //CONFIGURATION
    //-----------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //enable the up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureAlarmManager() {
        Intent alarmIntent = new Intent(NotificationsActivity.this, MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NotificationsActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void configureSwitchButton() {
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtils.isEmpty(editText.getText().toString()) && !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                        && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked() || TextUtils.isEmpty(editText.getText().toString()) ||
                        !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                                && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked()) {
                    Toast.makeText(getApplicationContext(), "You have to enter at least one keyword and check one category!", Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                } else {
                    if (isChecked) {
                        getCheckedCheckboxes();
                        preferences.edit().putString(KEYWORD_NOTIFICATION, editText.getText().toString()).apply();
                        startAlarm();
                        preferences.edit().putInt(SWITCH_BUTTON_STATE, 1).apply();
                        System.out.println("state = " + preferences.getInt(SWITCH_BUTTON_STATE, -1));
                    } else {
                        stopAlarm();
                        //uncheck the boxes, erase the edit text
                        editText.setText(null);
                        preferences.edit().putInt(SWITCH_BUTTON_STATE, 0).apply();
                        System.out.println("state 2  === " + preferences.getInt(SWITCH_BUTTON_STATE, -1));
                    }
                }
            }
        });
    }

    //------------------------------------------
    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000,
                10000, pendingIntent);
        Toast.makeText(this, "Notifications on!", Toast.LENGTH_SHORT).show();
    }

    private void stopAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Notifications off!", Toast.LENGTH_SHORT).show();
    }

    public void getCheckedCheckboxes() {
        if (checkboxArts.isChecked()) {
            preferences.edit().putString(ARTS_NOTIFICATION, "arts").apply();
        }
        if (checkboxPolitics.isChecked()) {
            preferences.edit().putString(POLITICS_NOTIFICATION, "politics").apply();
        }
        if (checkboxBusiness.isChecked()) {
            preferences.edit().putString(BUSINESS_NOTIFICATION, "business").apply();
        }
        if (checkboxSports.isChecked()) {
            preferences.edit().putString(SPORTS_NOTIFICATION, "sports").apply();
        }
        if (checkboxEntrepreneurs.isChecked()) {
            preferences.edit().putString(ENTREPRENEURS_NOTIFICATION, "entrepreneurs").apply();
        }
        if (checkboxTravel.isChecked()) {
            preferences.edit().putString(TRAVEL_NOTIFICATION, "travel").apply();
        }
    }

    private void setPreferencesToNull() {
        preferences.edit().putString(KEYWORD_NOTIFICATION, null).apply();
        preferences.edit().putString(ARTS_NOTIFICATION, null).apply();
        preferences.edit().putString(POLITICS_NOTIFICATION, null).apply();
        preferences.edit().putString(BUSINESS_NOTIFICATION, null).apply();
        preferences.edit().putString(SPORTS_NOTIFICATION, null).apply();
        preferences.edit().putString(ENTREPRENEURS_NOTIFICATION, null).apply();
        preferences.edit().putString(TRAVEL_NOTIFICATION, null).apply();
    }

    //--------------------------------------------

    private void displayLastKeyword() {
        if (preferences.getString(KEYWORD_NOTIFICATION, null) != null) {
            editText.setText(preferences.getString(KEYWORD_NOTIFICATION, null));
        }
    }

    private void displayLastSwitchButtonState() {
        int state = preferences.getInt(SWITCH_BUTTON_STATE, -1);
        System.out.println("state now = " +preferences.getInt(SWITCH_BUTTON_STATE, -1));
        if (state == 0) {
            switchButton.setChecked(false);
        } else if (state == 1) {
            switchButton.setChecked(true);
        }
    }


}
