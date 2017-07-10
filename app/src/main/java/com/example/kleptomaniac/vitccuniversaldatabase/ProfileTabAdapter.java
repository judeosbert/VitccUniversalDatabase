package com.example.kleptomaniac.vitccuniversaldatabase;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by kleptomaniac on 24/6/17.
 */

public class ProfileTabAdapter extends FragmentStatePagerAdapter {
    private final String[] PAGES_TITLES = new String[]{"Information","Requests","Settings","Developer"};
    private final Fragment[] PAGES = new Fragment[]{
            new ProfileInformationTab(),
            new ProfileRequestsTab(),
            new ProfileSettingsTab(),
            new ProfileAboutTab()
    };


    public ProfileTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return  PAGES[position];
    }

    @Override
    public int getCount() {
        return PAGES.length;
    }

    @Override
        public CharSequence getPageTitle(int position)
        {
         return PAGES_TITLES[position];
        }

}
