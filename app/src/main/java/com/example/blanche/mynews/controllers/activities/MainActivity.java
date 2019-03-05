package com.example.blanche.mynews.controllers.activities;


import android.content.Intent;
import android.drm.DrmStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.PageAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureToolbar();
        configureViewpagerAndTabs();
        configureNavigationView();
        configureDrawerLayout();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_main_search:
                //on démarre l'activité de search
                Intent searchActivity = new Intent(this, SearchActivity.class);
                startActivity(searchActivity);
                return true;
            case R.id.menu_main_notifications:
                Intent notificationsActivity = new Intent(this, NotificationsActivity.class);
                startActivity(notificationsActivity);
                return true;
            case R.id.menu_main_help:
                Toast.makeText(this, "pas encore implémenté...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_main_about:
                Intent aboutActivity = new Intent(this, AboutActivity.class);
                startActivity(aboutActivity);
                default:
                    return super.onOptionsItemSelected(menuItem);
        }
    }

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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
                break;
            case R.id.business_category:
                break;
            case R.id.entrepreneurs_category:
                break;
            case R.id.politics_category:
                break;
            case R.id.sports_category:
                break;
            case R.id.travel_category:
                break;
                default:
                    break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}