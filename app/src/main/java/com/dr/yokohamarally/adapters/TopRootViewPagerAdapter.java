package com.dr.yokohamarally.adapters;

import android.app.Fragment;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dr.yokohamarally.flagments.AllRootFragment;

public class TopRootViewPagerAdapter extends FragmentPagerAdapter{

    private final Resources mResources;

    public TopRootViewPagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        mResources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new AllRootFragment();
                break;
            case 1:
                fragment = new AllRootFragment();
                break;
        }



        return fragment;
    }


}
