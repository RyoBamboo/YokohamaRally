

package com.dr.yokohamarally.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.dr.yokohamarally.fragments.NewRootFragment;
import com.dr.yokohamarally.fragments.RecoRootFragment;
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
    private float currentX;

    private TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;

    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentTabHost tabHost;
    private int currentTab;

    @Override
    protected void onResume() {
        boolean isComp = getIntent().getBooleanExtra("isComp", false);
        if (isComp == true) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            sp.edit().remove("isCompleted").commit();
            sp.edit().remove("rootId").commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_tab);


        /*------------------------
         * ログインしているか確認する
         * 現在は強制的にLoginActivityへ
        /*----------------------*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (LoginActiviry.isLogin(sp) == false) {
            Intent intent = new Intent(MainActivity.this, LoginActiviry.class);
            startActivity(intent);
        }
      
        /*-------------------------
         * FragmentTabHostによる実装
         *-----------------------*/
        // FragmentTabHostの取得
        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
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
        tabHost.addTab(tabSpec2, NewRootFragment.class, null);

        tabSpec3 = tabHost.newTabSpec("tab3");
        tabSpec3.setIndicator("オススメ");
        // TabHostに追加
        tabHost.addTab(tabSpec3, RecoRootFragment.class, null);


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





        /*-------------------------
         * サイドバーのリスト作成
         *-----------------------*/
        String[] members = { "マイぺージ",  "設定", "マイラリー投稿","挑戦中ページ","ログアウト" };

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
                if("マイぺージ".equals(item)){
                    Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                    startActivity(intent);

                }else if("設定".equals(item)){
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }else if("挑戦中ページ".equals(item)){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    int tryId = sp.getInt("rootId", 0);
                    if(tryId != 0 ){
                        Intent intent = new Intent(MainActivity.this, TryActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "現在挑戦していません", Toast.LENGTH_SHORT).show();
                    }
                }else if("マイラリー投稿".equals(item)){
                    Intent intent = new Intent(MainActivity.this, FormActivity.class);
                    intent.putExtra("remove",1 );
                    startActivity(intent);
                 }else if("ログアウト".equals(item)){
                    Logout();
                }
            }
        });




    }

    //アクションバーメニューセレクト
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

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



    public void Logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("ログアウトしますか？")
                .setPositiveButton(
                        "はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ログアウトしてログインページへ
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                sp.edit().putBoolean("isLogin", false).commit();
                                Intent intent = new Intent(MainActivity.this, LoginActiviry.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(
                        "いいえ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .show();
    }

    //フリック動作によるタブの切替
//    protected float lastTouchX;
//    protected float limitx = 180;
//    @Override
//    public boolean dispatchTouchEvent( MotionEvent event ){
//        switch( event.getAction() ){
//            case MotionEvent.ACTION_DOWN:
//                lastTouchX = event.getX();
//                System.out.println("right");
//                break;
//
//            case MotionEvent.ACTION_UP:
//                float diff = lastTouchX - event.getX();
//                if( diff > limitx ){
//
//                    if(currentTab<2)currentTab++;
//                    tabHost.setCurrentTab(currentTab);
//                    return true;
//                }
//                if( diff < -1 * limitx ){
//                    System.out.println("left");
//                    if(currentTab>0)currentTab--;
//                    tabHost.setCurrentTab(currentTab);
//                    return true;
//                }
//                break;
//        }
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onKeyDown(int keyCode , KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){

            Toast.makeText(this, "トップページでの戻るボタンは無効です", Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }
}
