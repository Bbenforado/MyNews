package com.example.blanche.mynews.controllers.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.PageAdapter;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    Bundle bundle;
    SharedPreferences preferences;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String KEY_BUTTON = "key_button";
    public static final String KEY_ACTIVITY = "key_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) {
            setContentView(R.layout.activity_main);
            preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
            configureToolbar();
            configureViewpagerAndTabs();
            configureNavigationView();
            configureDrawerLayout();
            bundle = new Bundle();
        } else {
            setContentView(R.layout.layout_no_internet);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_main_search:
                preferences.edit().putInt(KEY_ACTIVITY, 0).apply();
                launchSearchActivity();
                return true;
            case R.id.menu_main_notifications:
                //launchNotificationsActivity();
                preferences.edit().putInt(KEY_ACTIVITY, 1).apply();
                launchSearchActivity();
                return true;
            case R.id.menu_main_help:
                launchHelpActivity();
                return true;
            case R.id.menu_main_about:
                launchAboutActivity();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    //---------------------
    //CONFIGURATION
    //----------------------------

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureViewpagerAndTabs() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.textsFragments)) {
        });
        TabLayout tabs = findViewById(R.id.main_tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    private void configureNavigationView() {
        navigationView = findViewById(R.id.main_activity_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.main_activity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();
        switch (id) {
            case R.id.art_category:
                //launch activity that displays a list of art articles
                bundle.putString(KEY_BUTTON, "arts");
                launchSearchedArticleActivity();
                break;
            case R.id.business_category:
                //launch activity that displays a list of business articles
                bundle.putString(KEY_BUTTON, "business");
                launchSearchedArticleActivity();
                break;
            case R.id.entrepreneurs_category:
                //launch activity that displays a list of entrepreneurs articles
                bundle.putString(KEY_BUTTON, "entrepreneurs");
                launchSearchedArticleActivity();
                break;
            case R.id.politics_category:
                //launch activity that displays a list of politics articles
                bundle.putString(KEY_BUTTON, "politics");
                launchSearchedArticleActivity();
                break;
            case R.id.sports_category:
                //launch activity that displays a list of sports articles
                bundle.putString(KEY_BUTTON, "sports");
                launchSearchedArticleActivity();
                break;
            case R.id.travel_category:
                //launch activity that displays a list of travels articles
                bundle.putString(KEY_BUTTON, "travel");
                launchSearchedArticleActivity();
                break;
            default:
                break;
        }
        uncheckItemOfNavigationDrawer();
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //-----------------

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void launchSearchedArticleActivity() {
        Intent searchArticleActivity = new Intent(this, ArticlesByCategoryActivity.class);
        searchArticleActivity.putExtras(bundle);
        startActivity(searchArticleActivity);
    }

    private void launchHelpActivity() {
        Intent helpActivity = new Intent(this, HelpActivity.class);
        startActivity(helpActivity);
    }

    private void launchAboutActivity() {
        Intent aboutActivity = new Intent(this, AboutActivity.class);
        startActivity(aboutActivity);
    }

    private void launchSearchActivity() {
        Intent searchActivity = new Intent(this, SearchActivity.class);
        startActivity(searchActivity);
    }

    private void uncheckItemOfNavigationDrawer() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }


}
