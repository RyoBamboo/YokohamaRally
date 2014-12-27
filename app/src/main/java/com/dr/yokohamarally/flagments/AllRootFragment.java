package com.dr.yokohamarally.flagments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.cores.Injector;

import butterknife.InjectView;

public class AllRootFragment extends Fragment {

    @InjectView(R.id.all_root_list_view)
    ListView mAllTootListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



}
