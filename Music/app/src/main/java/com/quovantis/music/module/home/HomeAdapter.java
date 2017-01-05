package com.quovantis.music.module.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * View Pager adapter for adding fragments to view pager {@link HomeActivity}
 */

class HomeAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentsList;
    private List<CharSequence> mTitlesList;

    HomeAdapter(FragmentManager fm) {
        super(fm);
        mFragmentsList = new ArrayList<>(3);
        mTitlesList = new ArrayList<>(3);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }

    void addFragment(Fragment fragment, CharSequence title) {
        mFragmentsList.add(fragment);
        mTitlesList.add(title);
    }

    Fragment getFragment(int pos) {
        return mFragmentsList.get(pos);
    }
}
