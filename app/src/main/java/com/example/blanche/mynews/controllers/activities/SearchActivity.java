package com.example.blanche.mynews.controllers.activities;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.example.blanche.mynews.controllers.utils.GetArticlesWorker;
import com.example.blanche.mynews.R;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    //BINDVIEWS

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
    public static final String ARTS = "arts";
    public static final String POLITICS = "politics";
    public static final String BUSINESS = "business";
    public static final String SPORTS = "sports";
    public static final String ENTREPRENEURS = "entrepreneurs";
    public static final String TRAVEL = "travel";

    public static final String ARTS_NOTIF = "artsNotif";
    public static final String POLITICS_NOTIF = "politicsNotif";
    public static final String BUSINESS_NOTIF = "businessNotif";
    public static final String SPORTS_NOTIF = "sportsNotif";
    public static final String ENTREPRENEURS_NOTIF = "entrepreneursNotif";
    public static final String TRAVEL_NOTIF = "travelNotif";

    public static final String KEYWORD_SEARCH = "keyword";
    public static final String KEYWORD_NOTIFICATION = "keyword_notif";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    private DatePickerDialog datePickerDialog;
    private String currentDate;
    ActionBar actionBar;
    SharedPreferences preferences;
    public static final String SWITCH_BUTTON_STATE = "state";
    PeriodicWorkRequest periodicRequest;
    Data data;
    public static final String IS_THE_FIRST_NOTIFICATION = "notification";

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
        if (preferences.getString(BEGIN_DATE, null) != null) {
            beginDateButton.setText(changeSavedDateFormat(preferences.getString(BEGIN_DATE, null)));
        }
        if (preferences.getString(END_DATE, null) != null) {
            endDateButton.setText(changeSavedDateFormat(preferences.getString(END_DATE, null)));
        }
    }

    public String changeSavedDateFormat(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String result = day + "/" + month + "/" + year;
        return result;
    }

    //NOTIFICATION ACTIVITY
    //-----------------------------
    private void configureSwitchButton() {
        displaySwitchButtonState();

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!paramsAreMissing()) {

                    if (isChecked) {
                        preferences.edit().putString(IS_THE_FIRST_NOTIFICATION, "true").apply();
                        preferences.edit().putString(KEYWORD_NOTIFICATION, editText.getText().toString()).apply();
                        preferences.edit().putInt(SWITCH_BUTTON_STATE, 0).apply();
                        getCheckedCheckboxes();
                        configureWorkRequest();
                        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic_work", ExistingPeriodicWorkPolicy.REPLACE, periodicRequest);

                    } else {
                        //uncheck the boxes, erase the edit text, and stop the periodic job
                        editText.setText(null);
                        uncheckCheckBoxes();
                        preferences.edit().putInt(SWITCH_BUTTON_STATE, 1).apply();
                        preferences.edit().putString(KEYWORD_NOTIFICATION, null).apply();
                        preferences.edit().putString(CATEGORIES_NOTIFICATION, null).apply();
                        WorkManager.getInstance().cancelAllWorkByTag("periodicJob");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You have to enter at least one keyword and check one category!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configureWorkRequest() {
        data = new Data.Builder()
                .putString(GetArticlesWorker.CATEGORIES_WORKER, preferences.getString(CATEGORIES_NOTIFICATION, null))
                .putString(GetArticlesWorker.KEYWORD_WORKER, preferences.getString(KEYWORD_NOTIFICATION, null))
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        periodicRequest = new PeriodicWorkRequest.Builder(GetArticlesWorker.class, 15, TimeUnit.MINUTES)
                .addTag("periodicJob")
                .setConstraints(constraints)
                .setInputData(data)
                .build();
    }

    //---------------
    //ACTIONS
    //---------------

    //SEARCH ACTIVITY
    //--------------------------------
    @OnClick(R.id.search_button)
    public void submit(View view) {
        if (!paramsAreMissing()) {
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
                if (year< currentYear) {
                    saveDates(v, dayOfMonth, month, year);
                } else if(year == currentYear) {
                    if ((month+1) < currentMonth) {
                        saveDates(v, dayOfMonth, month, year);
                    } else if ((month+1) == currentMonth) {
                        if (dayOfMonth <= currentDay) {
                            saveDates(v, dayOfMonth, month, year);
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

    private  void saveDates(View v, int dayOfMonth, int month, int year) {
            String strMonth = addZeroToDate(Integer.toString(month + 1));
            String strDay = addZeroToDate(Integer.toString(dayOfMonth));
            String strYear = Integer.toString(year);
            switch (v.getId()) {
                case R.id.spinner_button_start_date:
                    beginDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                    preferences.edit().putString(BEGIN_DATE, strYear + strMonth + strDay).apply();
                    break;
                case R.id.spinner_button_end_date:
                        if (isBeginDateBeforeEndDate(dayOfMonth, month, year)) {
                            endDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                            preferences.edit().putString(END_DATE, strYear + strMonth + strDay).apply();
                        } else {
                            Toast.makeText(this, "You have to select a date after the begin date...", Toast.LENGTH_SHORT).show();
                        }
                    break;
                default:
                    break;
            }
    }

    private boolean isBeginDateBeforeEndDate(int day, int month, int year) {
        String dateBegin = beginDateButton.getText().toString();
        String dateEnd = String.valueOf(day + "/" + (month+1) + "/" + year);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Date date1 = format.parse(dateBegin,new ParsePosition(0));
        Date date2 = format.parse(dateEnd, new ParsePosition(0));

        if (date1.compareTo(date2) <= 0){
            return true;
        } else {
            return false;
        }
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

    private void launchSearchArticlesActivity() {
        Intent searchArticlesActivity = new Intent(this, SearchArticlesActivity.class);
        startActivity(searchArticlesActivity);
    }

    //FOR NOTIFICATIONS
    //------------------------------------
    private void displaySwitchButtonState() {
        if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
            switchButton.setChecked(true);
        } else {
            switchButton.setChecked(false);
        }
    }

    //FOR BOTH
    //---------------------------------

    private boolean paramsAreMissing() {
        if (TextUtils.isEmpty(editText.getText().toString()) && !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked() || TextUtils.isEmpty(editText.getText().toString()) ||
                !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                        && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked()) {

            switchButton.setChecked(false);
            return true;
        } else {
            return false;
        }
    }

    public void getCheckedCheckboxes() {
        StringBuilder stringBuilder = new StringBuilder();
        if (checkboxArts.isChecked()) {
            stringBuilder.append('"' + "arts" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(ARTS, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(ARTS_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(ARTS, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(ARTS_NOTIF, null).apply();
            }
        }
        if (checkboxPolitics.isChecked()) {
            stringBuilder.append(" " + '"' + "politics" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(POLITICS, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(POLITICS_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(POLITICS, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(POLITICS_NOTIF, null).apply();
            }
        }
        if (checkboxBusiness.isChecked()) {
            stringBuilder.append(" " + '"' + "business" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(BUSINESS, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(BUSINESS_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(BUSINESS, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(BUSINESS_NOTIF, null).apply();
            }
        }
        if (checkboxSports.isChecked()) {
            stringBuilder.append(" " + '"' + "sports" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(SPORTS, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(SPORTS_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(SPORTS, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(SPORTS_NOTIF, null).apply();
            }
        }
        if (checkboxEntrepreneurs.isChecked()) {
            stringBuilder.append(" " + '"' + "entrepreneurs" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(ENTREPRENEURS, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(ENTREPRENEURS_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(ENTREPRENEURS, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(ENTREPRENEURS_NOTIF, null).apply();
            }
        }
        if (checkboxTravel.isChecked()) {
            stringBuilder.append(" " + '"' + "travel" + '"');
            if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
                preferences.edit().putString(TRAVEL, "true").apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(TRAVEL_NOTIF, "true").apply();
            }
        } else {
            if (preferences.getInt(KEY_ACTIVITY,-1) == 0) {
                preferences.edit().putString(TRAVEL, null).apply();
            } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
                preferences.edit().putString(TRAVEL_NOTIF, null).apply();
            }
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
            if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
                setSavedCheckedCategories();
            }

        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            switchButton.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            setSavedCheckedCategories();
        }
    }

    private void uncheckCheckBoxes() {
        checkboxArts.setChecked(false);
        checkboxPolitics.setChecked(false);
        checkboxBusiness.setChecked(false);
        checkboxSports.setChecked(false);
        checkboxEntrepreneurs.setChecked(false);
        checkboxTravel.setChecked(false);
    }

    private void setSavedCheckedCategories() {
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            setCheckedCategoriesForSearchPage();
        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            setCheckedCategoriesForNotificationPage();
        }
    }

    private void setCheckedCategoriesForSearchPage() {
        if (preferences.getString(ARTS, null) != null) {
            checkboxArts.setChecked(true);
        }
        if (preferences.getString(POLITICS, null) != null) {
            checkboxPolitics.setChecked(true);
        }
        if (preferences.getString(BUSINESS, null) != null) {
            checkboxBusiness.setChecked(true);
        }
        if (preferences.getString(SPORTS, null) != null) {
            checkboxSports.setChecked(true);
        }
        if (preferences.getString(ENTREPRENEURS, null) != null) {
            checkboxEntrepreneurs.setChecked(true);
        }
        if (preferences.getString(TRAVEL, null) != null) {
            checkboxTravel.setChecked(true);
        }
    }

    private void setCheckedCategoriesForNotificationPage() {
        if (preferences.getString(ARTS_NOTIF, null) != null) {
            checkboxArts.setChecked(true);
        }
        if (preferences.getString(POLITICS_NOTIF, null) != null) {
            checkboxPolitics.setChecked(true);
        }
        if (preferences.getString(BUSINESS_NOTIF, null) != null) {
            checkboxBusiness.setChecked(true);
        }
        if (preferences.getString(SPORTS_NOTIF, null) != null) {
            checkboxSports.setChecked(true);
        }
        if (preferences.getString(ENTREPRENEURS_NOTIF, null) != null) {
            checkboxEntrepreneurs.setChecked(true);
        }
        if (preferences.getString(TRAVEL_NOTIF, null) != null) {
            checkboxTravel.setChecked(true);
        }
    }


}
