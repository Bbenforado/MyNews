package com.example.blanche.mynews.controllers.activities;

import android.app.DatePickerDialog;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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

/**
 * Activity that displays the searching tool
 */
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
    public static final String KEYWORD_SEARCH = "keyword";
    public static final String KEYWORD_NOTIFICATION = "keyword_notif";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    private DatePickerDialog datePickerDialog;
    private String currentDate;
    private ActionBar actionBar;
    private SharedPreferences preferences;
    public static final String SWITCH_BUTTON_STATE = "state";
    private PeriodicWorkRequest periodicRequest;
    public static final String IS_THE_FIRST_NOTIFICATION = "notification";
    public static final String SEARCH_CATEGORY_STATE = "search_state";
    public static final String NOTIF_CATEGORY_STATE = "notif_state";
    private boolean[] searchCategoryState = new boolean[6];
    private boolean[] notifCategoryState = new boolean[6];
    private String[] categories = {"arts", "politics", "business", "sports", "entrepreneurs", "travel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        configureToolbar();
        displayNotificationOrSearchScreen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //here we have to save the preferences, keyword and categories
        //for search activity
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            saveData();
            if (editText.getText().toString().length() == 0) {
                preferences.edit().putString(KEYWORD_SEARCH, null).apply();
            }
        }
        //for notification activity
        if (preferences.getInt(KEY_ACTIVITY, -1) ==1) {
            if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
                saveDataForNotificationActivity(true);
            } else if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 1) {
                saveDataForNotificationActivity(false);
            }
        }
    }

    //----------------
    //CONFIGURATION
    //----------------
    //FOR BOTH
    //---------------------------
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
        if (preferences.getString(KEYWORD_SEARCH, null) != null) {
            if (preferences.getString(BEGIN_DATE, null) != null) {
                beginDateButton.setText(changeSavedDateFormat(preferences.getString(BEGIN_DATE, null)));
            }
            if (preferences.getString(END_DATE, null) != null) {
                endDateButton.setText(changeSavedDateFormat(preferences.getString(END_DATE, null)));
            }
        } else {
            beginDateButton.setHint(getCurrentDate());
            endDateButton.setHint(getCurrentDate());
            preferences.edit().putString(BEGIN_DATE, null).apply();
            preferences.edit().putString(END_DATE, null).apply();
            uncheckCheckBoxes();
        }
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
                        //begin the work request
                        preferences.edit().putString(IS_THE_FIRST_NOTIFICATION, "true").apply();
                        saveDataForNotificationActivity(true);
                        configureWorkRequest();
                        WorkManager.getInstance().enqueueUniquePeriodicWork("periodic_work", ExistingPeriodicWorkPolicy.REPLACE, periodicRequest);

                    } else {
                        //uncheck the boxes, erase the edit text, and stop the periodic job
                        editText.setText(null);
                        uncheckCheckBoxes();
                        saveDataForNotificationActivity(false);
                        WorkManager.getInstance().cancelAllWorkByTag("periodicJob");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You have to enter at least one keyword and check one category!", Toast.LENGTH_SHORT).show();
                    switchButton.setChecked(false);
                }
            }
        });
    }

    private void configureWorkRequest() {
        Data data = new Data.Builder()
                .putString(GetArticlesWorker.CATEGORIES_WORKER, preferences.getString(CATEGORIES_NOTIFICATION, null))
                .putString(GetArticlesWorker.KEYWORD_WORKER, preferences.getString(KEYWORD_NOTIFICATION, null))
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        periodicRequest = new PeriodicWorkRequest.Builder(GetArticlesWorker.class, 24, TimeUnit.HOURS)
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
            saveData();
            launchSearchArticlesActivity();
        } else {
            Toast.makeText(this, R.string.toast_text_no_keyword_no_checked_category, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.spinner_button_start_date, R.id.spinner_button_end_date})
    public void chooseDates(View view) {
        createDatePickerDialog(view);
        datePickerDialog.show();
    }
    //------------------------------------------------------------
    //METHODS FOR SEARCH ACTIVITY
    //--------------------------------
    /**
     * displays a datePickerDialog, and saves the selected date (begin and end date)
     * @param v
     */
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

    /**
     * check if the selected date is after the selected begin date
     * @param day
     * @param month
     * @param year
     * @return true if the end date is after the begin date
     *          false if the end date is before the begin date
     */
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

    /**
     * takes in param a date (string) format yyyyMMdd and return a string format dd/MM/yyyy
     * @param date
     * @return
     */
    public String changeSavedDateFormat(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String result = day + "/" + month + "/" + year;
        return result;
    }

    /**
     * display a toast message which says that the selected date is not correct
     * set the current date to the button
     * @param v
     */
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

    /**
     * return the current date in a string, pattern is : dd/MM/yy
     * @return
     */
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
    /**
     * display the current button state, on or off, depending on what is saved in preferences
     */
    private void displaySwitchButtonState() {
        if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
            if (paramsAreMissing()) {
                switchButton.setChecked(false);
            } else {
                switchButton.setChecked(true);
            }
        } else {
            switchButton.setChecked(false);
        }
    }

    //FOR BOTH
    //---------------------------------
    /**
     * verify that keyword is written AND a category is checked
     * @return true: if the params are missing
     *         false: if no params are missing
     */
    private boolean paramsAreMissing() {
        return TextUtils.isEmpty(editText.getText().toString()) && !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked() || TextUtils.isEmpty(editText.getText().toString()) ||
                !checkboxArts.isChecked() && !checkboxPolitics.isChecked() && !checkboxBusiness.isChecked()
                        && !checkboxSports.isChecked() && !checkboxEntrepreneurs.isChecked() && !checkboxTravel.isChecked();
    }

    /**
     * set the text of the edittext with the keyword saved in preferences, depending on activity displayed
     * set default hint if no keywords are saved
     */
    private void setCurrentKeyword() {
        if (preferences.getString(KEYWORD_SEARCH, null) != null && preferences.getInt(KEY_ACTIVITY, -1) != 1) {
            editText.setText(preferences.getString(KEYWORD_SEARCH, null));
        } else {
            editText.setHint(R.string.query_item);
        }
        if (preferences.getString(KEYWORD_NOTIFICATION, null) != null && preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            editText.setText(preferences.getString(KEYWORD_NOTIFICATION, null));
        } else {
            editText.setHint(R.string.query_item);
        }
    }

    /**
     * displays the correct activity (notification or search) depending on KEY_ACTIVITY
     */
    public void displayNotificationOrSearchScreen() {
        if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            //display notification activity
            layoutDates.setVisibility(View.GONE);
            layoutSpinners.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            switchButton.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.VISIBLE);
            actionBar.setTitle("Notifications");
            if (preferences.getInt(SWITCH_BUTTON_STATE, -1) == 0) {
                setCategoriesState();
                setCurrentKeyword();
            }
            configureSwitchButton();
        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            //display search activity
            switchButton.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            layoutDates.setVisibility(View.VISIBLE);
            layoutSpinners.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            setCategoriesState();
            configureDatesButtons();
            setCurrentKeyword();
        }

    }

    /**
     * unchecked all the boxes
     */
    public void uncheckCheckBoxes() {
        checkboxArts.setChecked(false);
        checkboxPolitics.setChecked(false);
        checkboxBusiness.setChecked(false);
        checkboxSports.setChecked(false);
        checkboxEntrepreneurs.setChecked(false);
        checkboxTravel.setChecked(false);
    }

    //-----------------------------
    //SAVE DATA
    //-----------------------------------------
    //FOR BOTH
    //----------------------
    /**
     * creates a string with all categories checked
     * saves it in preferences
     * saves if a checkbox is checked or not
     * saves it in preferences with gson
     * @param stateOfCheckboxes array of boolean for state of checkboxes: true if checkbox is checked, false if not
     */
    private void saveCategoriesState(boolean[] stateOfCheckboxes) {
        StringBuilder stringBuilder = new StringBuilder();
        String categoriesSelected = "";
        Gson gson = new Gson();
        CheckBox[] checkBoxes = {checkboxArts, checkboxPolitics, checkboxBusiness, checkboxSports, checkboxEntrepreneurs, checkboxTravel};
        for (int i = 0; i < checkBoxes.length; i++) {
            stateOfCheckboxes[i] = checkBoxes[i].isChecked();
            if (stateOfCheckboxes[i]) {
                stringBuilder.append('"' + categories[i] + '"' + " ");
                categoriesSelected = stringBuilder.toString();
            }
        }
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            //we save the string for the request
            preferences.edit().putString(CATEGORIES_SEARCH, categoriesSelected).apply();
            //we save the array of state of checkboxes
            preferences.edit().putString(SEARCH_CATEGORY_STATE, gson.toJson(stateOfCheckboxes)).apply();

        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            //we save the string for the request
            preferences.edit().putString(CATEGORIES_NOTIFICATION, categoriesSelected).apply();
            preferences.edit().putString(NOTIF_CATEGORY_STATE, gson.toJson(stateOfCheckboxes)).apply();
        }
    }

    /**
     * retrieve the state of the checkboxes (checked or not) and check/uncheck them
     */
    private void setCategoriesState() {
        CheckBox[] checkBoxes = {checkboxArts, checkboxPolitics, checkboxBusiness, checkboxSports, checkboxEntrepreneurs, checkboxTravel};
        boolean[] arrayOfState;
        String json = "";
        Gson gson = new Gson();
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            json = preferences.getString(SEARCH_CATEGORY_STATE, null);
        } else if (preferences.getInt(KEY_ACTIVITY, -1) == 1) {
            json = preferences.getString(NOTIF_CATEGORY_STATE, null);
        }
        Type type = new TypeToken<boolean[]>() {}.getType();
        arrayOfState = gson.fromJson(json, type);
        if (arrayOfState == null) {
            arrayOfState = new boolean[6];
        }
        for (int i = 0; i < checkBoxes.length; i++) {
            //we check the box if it's true and uncheck if false
            checkBoxes[i].setChecked(arrayOfState[i]);
        }
    }

    //--------------
    //FOR SEARCH ACTIVITY
    //-----------------------
    /**
     * save the selected date when click on the OK button of the datePickerDialog
     * @param v the view we clicked on
     * @param dayOfMonth
     * @param month
     * @param year
     */
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
                if (beginDateButton.getText().toString().length() != 0) {
                    if (isBeginDateBeforeEndDate(dayOfMonth, month, year)) {
                        endDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                        preferences.edit().putString(END_DATE, strYear + strMonth + strDay).apply();
                    } else {
                        Toast.makeText(this, "You have to select a date after the begin date...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "You have to select a begin date first!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * save keyword and string of categories checked in preferences
     */
    public void saveData() {
        if (preferences.getInt(KEY_ACTIVITY, -1) == 0) {
            preferences.edit().putString(KEYWORD_SEARCH, editText.getText().toString()).apply();
            saveCategoriesState(searchCategoryState);
        }
    }

    //----------------------
    //FOR NOTIFICATION ACTIVITY
    //----------------------------
    /**
     * save keyword, string of checked categories and switch button state (on/off) in preferences
     * @param isChecked
     */
    public void saveDataForNotificationActivity(Boolean isChecked) {
        if (isChecked) {
            preferences.edit().putInt(SWITCH_BUTTON_STATE, 0).apply();
            preferences.edit().putString(KEYWORD_NOTIFICATION, editText.getText().toString()).apply();
            saveCategoriesState(notifCategoryState);
        } else {
            preferences.edit().putInt(SWITCH_BUTTON_STATE, 1).apply();
            preferences.edit().putString(KEYWORD_NOTIFICATION, null).apply();
            preferences.edit().putString(CATEGORIES_NOTIFICATION, null).apply();
        }
    }
}
