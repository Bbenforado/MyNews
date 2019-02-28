package com.example.blanche.mynews.controllers.activities;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.blanche.mynews.R;
import com.example.blanche.mynews.controllers.adapters.PageAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureViewpagerAndTabs();
    }

    private void configureViewpagerAndTabs() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.textsFragments)) {
        });

        TabLayout tabs = findViewById(R.id.main_tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }
}
