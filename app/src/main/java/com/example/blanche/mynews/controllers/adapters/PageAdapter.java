package com.example.blanche.mynews.controllers.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.blanche.mynews.controllers.fragments.PageFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private String[] texts;

    //-------------
    //CONTRUCTOR
    //-------------
    public PageAdapter(FragmentManager fm, String[] text) {
        super(fm);
        this.texts = text;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position, this.texts[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return texts[position];
    }
}
