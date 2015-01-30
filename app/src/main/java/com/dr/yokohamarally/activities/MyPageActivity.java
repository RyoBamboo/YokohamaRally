/**
 * RootTabActivity.java
 *
 * ルートのリスト一覧を表示するアクティビティ
 */

package com.dr.yokohamarally.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.MyData;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.adapters.MyPageAdapter;
import com.dr.yokohamarally.core.RequestManager;
import com.dr.yokohamarally.core.VolleyApi;
import com.dr.yokohamarally.fragments.AllRootFragment;
import com.dr.yokohamarally.fragments.GpsService;
import com.dr.yokohamarally.fragments.TryInformation;
import com.dr.yokohamarally.models.Root;
import com.dr.yokohamarally.adapters.RootAdapter;
import com.navdrawer.SimpleSideDrawer;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.OnItemClick;

public class MyPageActivity extends ActionBarActivity  {

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
    private MyPageAdapter mRootAdapter;
    private ListView mAllRootListView;
    private String mEmail;
    private String clearRoot;
    private String clearDate;
    private int totalClearRoot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        totalClearRoot++;

        // リストビューを使用する準備
        roots = new ArrayList<Root>();
        mRootAdapter = new MyPageAdapter(MyPageActivity.this, 0, roots);
        mAllRootListView = (ListView)findViewById(R.id.my_list);
        mAllRootListView.setAdapter(mRootAdapter);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mEmail = sp.getString("email", "");
        Log.d("email!", mEmail);



        //サイドバー指定
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
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
        String[] members = { "マイぺージ",  "設定", "その他","ログアウト" };

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
                if("トップページ".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if("設定".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, SettingActivity.class);
                    startActivity(intent);
                }else if("マイラリー投稿".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, FormActivity.class);
                    startActivity(intent);
                }else if("ログアウト".equals(item)){
                    Logout();
                }
            }
        });


        String profileImageStr = sp.getString("profile_image", "");

        ImageView profileImageView = (ImageView)findViewById(R.id.myimage);
        byte[] b = Base64.decode(profileImageStr, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        profileImageView.setImageBitmap(bmp);


        String url = "http://yokohamarally.prodrb.com/api/get_clear_info.php?email=";
        String params = String.valueOf(mEmail);
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();
        Log.d("a",uri);



        RequestManager.addRequest(new JsonObjectRequest(Request.Method.GET,uri, null, getEmailResponseListener(), errorListener()), this);

        try {
            Thread.sleep(360);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // http通信
        RequestManager.addRequest(new JsonObjectRequest(Request.Method.GET, VolleyApi.GET_ALL_ROOT_URL, null, responseListener(), errorListener()), this);




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


    public void Logout() {
        new AlertDialog.Builder(MyPageActivity.this)
                .setTitle("ログアウトしますか？")
                .setPositiveButton(
                        "はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ログアウトしてログインページへ
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                sp.edit().putBoolean("isLogin", false).commit();
                                Intent intent = new Intent(MyPageActivity.this, LoginActiviry.class);
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
    private Response.Listener<JSONObject> getEmailResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Root> _roots = new ArrayList<Root>();
                try {
                        JSONArray json_roots = response.getJSONArray("info");

                        JSONObject json_user = json_roots.getJSONObject(0);
                        Log.d("json",""+json_user);
                        clearRoot = json_user.getString("clear");
                        clearDate = json_user.getString("clearDate");
                        String name = json_user.getString("name");
                        TextView myName = (TextView)findViewById(R.id.my_name);
                        myName.setText(name);
                }catch (Exception e) {
                    System.out.println(e);
                }

            }
        };
    }



    private Response.Listener<JSONObject> responseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Root> _roots = new ArrayList<Root>();

                try {
                    JSONArray json_roots = response.getJSONArray("roots");
                    System.out.println(response);


                    String[] clearRoots = clearRoot.split(",", 0);
                    String[] clearDates = clearDate.split(",", 0);


                    // クリアしていたらlistViewに追加
                    for(int j = 0; j < clearRoots.length ; j ++ ){

                            JSONObject json_root = json_roots.getJSONObject(Integer.parseInt(clearRoots[j])-1);
                            System.out.println(json_root);

                            String title = json_root.getString("title");
                            int rate = json_root.getInt("rate");
                            int number = Integer.parseInt(clearRoots[j])-1;
                            String number_title = String.valueOf(number + 1) + ". " + title;
                            String imageUrl = json_root.getString("image_url");
                            Root root = new Root();
                            root.setClearDate(clearDates[j]);
                            root.setTitle(number_title);
                            root.setImageUrl(imageUrl);
                            root.setRate(rate);

                            _roots.add(root);

                    }

                    totalClearRoot= clearRoots.length;
                    // adapterに反映、追加
                    mRootAdapter.addAll(_roots);
                } catch (Exception e) {
                    System.out.println(e);
                }

                TextView countClear = (TextView)findViewById(R.id.clear_count);
                countClear.setText(""+totalClearRoot);
            }
        };


    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        };
    }
}
