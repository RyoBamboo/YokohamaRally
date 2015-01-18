/**
 * RootTabActivity.java
 *
 * ルートのリスト一覧を表示するアクティビティ
 */

package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.MyData;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.AllRootFragment;
import com.dr.yokohamarally.fragments.GpsService;
import com.dr.yokohamarally.models.Root;
import com.dr.yokohamarally.adapters.RootAdapter;
import com.navdrawer.SimpleSideDrawer;

import java.util.ArrayList;

import butterknife.OnItemClick;

public class MainActivity extends ActionBarActivity implements FragmentTabHost.OnTabChangeListener {

    private RequestQueue myQueue;

    private ArrayList<Root> roots;
    private RootAdapter adapter;
    private ListView rootListView;

    private MyData myData;

    private SimpleSideDrawer mNav;
    private float lastTouchX;
    private float currentX;

    private TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_tab);





        /*-------------------------
         * FragmentTabHostによる実装
         *-----------------------*/
        // FragmentTabHostの取得
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        // TabSpecの設定
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("人気順");
        // TabHostに追加
        tabHost.addTab(tabSpec1, AllRootFragment.class, null);

        // TabSpecの設定
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("新着順");
        // TabHostに追加
        tabHost.addTab(tabSpec2, AllRootFragment.class, null);

        tabSpec3 = tabHost.newTabSpec("tab2");
        tabSpec3.setIndicator("おすすめ");
        // TabHostに追加
        tabHost.addTab(tabSpec3, AllRootFragment.class, null);

        // リスナーに登録
        tabHost.setOnTabChangedListener(this);


        //サイドバー指定
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //アクションバーカスタム
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        //リスナー登録
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //サービス開始
        startService(new Intent(MainActivity.this, GpsService.class));



        String[] members = { "マイぺージ",  "設定", "その他" };

        ListView lv = (ListView) findViewById(R.id.sidebar_listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, members);

        lv.setAdapter(adapter);

        //リスト項目がクリックされた時の処理
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                if(item.equals("マイページ")){

                }
                else if(item.equals("設定")){

                }
            }
        });




    }

    //アクションバーメニューセレクト
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
            default:
                return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }


    }

    //アイコンアニメーション
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public void onTabChanged(String tabId) {
        Log.d("onTabChanged", "tabId: " + tabId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;  // アクションビューを折りたたむ為にtrueを返す
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;  // アクションビューを広げる為にtrueを返す
            }
        });
        return super.onCreateOptionsMenu(menu);
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



    public static class BlankFragment extends Fragment implements AdapterView.OnItemClickListener{

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

            View v = inflater.inflate(R.layout.fragment_all_root_list, container, false);

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
            rootListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);

            myData = new MyData(container.getContext(),myQueue,adapter);
            myData.getAllRoot();

            return v;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), RootSummaryActivity.class);
            intent.putExtra("rootId", position + 1);
            startActivity(intent);
        }
    }
}
