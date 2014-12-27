package com.dr.yokohamarally.flagments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dr.yokohamarally.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TopRootFragment extends Fragment {

    @InjectView(R.id.top_root_view_pager)
    ViewPager mViewPager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_root, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        mViewPager.setAdapter(new Top)
    }
}
