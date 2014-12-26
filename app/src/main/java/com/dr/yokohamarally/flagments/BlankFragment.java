package com.dr.yokohamarally.flagments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.MyData;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.models.Root;
import com.dr.yokohamarally.adapters.RootAdapter;

import java.util.ArrayList;


public class BlankFragment extends Fragment {

    private RequestQueue myQueue;

    private ArrayList<Root> roots;
    private RootAdapter adapter;
    private ListView rootListView;

    private MyData myData;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_blank, container, false);



        /*------------------
         リストビュー表示処理
        -----------------*/
        // queue
        myQueue = Volley.newRequestQueue(container.getContext());

        // arrayList
        roots = new ArrayList<Root>();

        // adapter
        adapter = new RootAdapter(container.getContext(), 0, roots);

        // ListView
        rootListView = (ListView)v.findViewById(R.id.root_list);
        rootListView.setAdapter(adapter);

        myData = new MyData(container.getContext(),myQueue,adapter);
        myData.getData();

        return v;
    }
}
