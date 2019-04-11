package com.example.blanche.mynews.controllers.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.blanche.mynews.controllers.fragments.PageFragment;
import com.example.blanche.mynews.controllers.fragments.SecondPageFragment;
import com.example.blanche.mynews.controllers.fragments.ThirdPageFragment;

public class PageAdapter extends FragmentPagerAdapter {

   private String[] texts;

    //-------------
    //CONTRUCTOR
    //-------------
    public PageAdapter(FragmentManager fm, String[] text) {
        super(fm);
        this.texts = text;
    }

    //-------------
    //METHODS
    //---------------
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PageFragment.newInstance(position);
            case 1:
                return SecondPageFragment.newInstance(position);
            case 2:
                return ThirdPageFragment.newInstance(position);
                default:
                    return PageFragment.newInstance(position);
        }
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
