/**
 * RootTabActivity.java
 *
 * ルートのリスト一覧を表示するアクティビティ
 */

package com.dr.yokohamarally.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.MyData;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.models.Root;
import com.dr.yokohamarally.adapters.RootAdapter;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

public class RootTabActivity extends ActionBarActivity implements FragmentTabHost.OnTabChangeListener {

    private RequestQueue myQueue;

    private ArrayList<Root> roots;
    private RootAdapter adapter;
    private ListView rootListView;

    private MyData myData;

    private SimpleSideDrawer mNav;
    private float lastTouchX;
    private float currentX;

    private TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_tab);


        //サイドバー機能実装
        mNav = new SimpleSideDrawer(this);
        //mNav.setLeftBehindContentView(R.layout.fragment_first_tab);


        /*-------------------------
         * FragmentTabHostによる実装
         *-----------------------*/
        // FragmentTabHostの取得
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        // TabSpecの設定
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("tab1");
        // TabHostに追加
        tabHost.addTab(tabSpec1, BlankFragment.class, null);

        // TabSpecの設定
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("tab2");
        // TabHostに追加
        tabHost.addTab(tabSpec2, BlankFragment.class, null);

        // リスナーに登録
        tabHost.setOnTabChangedListener(this);

        //スライド判定リスナー登録
        tabHost.setOnTouchListener(new FlickTouchListener());

    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d("onTabChanged", "tabId: " + tabId);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root_tab, menu);
        return true;
    }
    */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */



     /*-----------------------
          スライド判定処理
     -----------------*/

    private class FlickTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = event.getX();
                    break;

                case MotionEvent.ACTION_UP:
                    currentX = event.getX();
                    if (lastTouchX - currentX < -80) {
                        //サイドバー表示
                        mNav.toggleLeftDrawer();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    currentX = event.getX();
                    if (lastTouchX - currentX < -80) {
                        //サイドバー表示
                        mNav.toggleLeftDrawer();
                    }
                    break;
            }
            return true;
        }
    }

    public static class BlankFragment extends Fragment {

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
}
