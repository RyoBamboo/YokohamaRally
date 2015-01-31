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
import android.preference.PreferenceManager;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.adapters.MyPageAdapter;
import com.dr.yokohamarally.core.RequestManager;
import com.dr.yokohamarally.models.Root;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MyPageActivity extends ActionBarActivity  {


    private ActionBarDrawerToggle mDrawerToggle;

    private String userId;

    private MyPageAdapter compRootAdapter;
    private MyPageAdapter postRootAdapter;

    private ArrayList<Root> compRootList;
    private ArrayList<Root> postRootList;

    private SharedPreferences sp;

    @InjectView(R.id.comp_list)
    ListView compListView;

    @InjectView(R.id.post_list)
    ListView postListView;

    @InjectView(R.id.comp_count)
    TextView compTextView;

    @InjectView(R.id.post_count)
    TextView postTextView;

    @InjectView(R.id.my_name)
    TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        ButterKnife.inject(this);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sp.getString("id", "");

        compRootList = new ArrayList<Root>();
        postRootList = new ArrayList<Root>();

        compRootAdapter = new MyPageAdapter(MyPageActivity.this, 0, compRootList);
        postRootAdapter = new MyPageAdapter(MyPageActivity.this, 0, postRootList);

        compListView.setAdapter(compRootAdapter);
        postListView.setAdapter(postRootAdapter);


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
        String[] members = { "トップぺージ",  "設定", "マイラリー投稿","挑戦中ページ","ログアウト" };

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
                if("トップぺージ".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if("設定".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, SettingActivity.class);
                    startActivity(intent);
                }else if("挑戦中ページ".equals(item)){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    int tryId = sp.getInt("rootId", 0);
                    if(tryId != 0 ){
                        Intent intent = new Intent(MyPageActivity.this, TryActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "現在挑戦していません", Toast.LENGTH_SHORT).show();
                    }
                }else if("マイラリー投稿".equals(item)){
                    Intent intent = new Intent(MyPageActivity.this, FormActivity.class);
                    intent.putExtra("remove",1 );
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

        // http通信
        String url = "http://yokohamarally.prodrb.com/api/get_mypage_info.php?user_id=" + userId;
        RequestManager.addRequest(new JsonObjectRequest(Request.Method.GET, url, null, responseListener(), errorListener()), this);
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

    // 通信処理
    private Response.Listener<JSONObject> responseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Root> _compRootList = new ArrayList<Root>();
                ArrayList<Root> _postRootList = new ArrayList<Root>();

                try {
                    JSONArray jsonCompRoots = response.getJSONArray("completed_roots");
                    JSONArray jsonPostRoots = response.getJSONArray("posted_roots");

                    String[] compDate = response.getString("clear_date").split(",");
                    nameTextView.setText(response.getString("name"));

                    int compCount = jsonCompRoots.length(); // クリアした数をセット
                    int postCount = jsonPostRoots.length(); // クリアした数をセット

                    compTextView.setText(String.valueOf(compCount));
                    postTextView.setText(String.valueOf(postCount));


                    // クリアしたルートの取得
                    for (int i = 0; i < compCount; i++) {
                        Root root = new Root();
                        JSONObject jsonCompRoot = jsonCompRoots.getJSONObject(i);

                        root.setTitle(jsonCompRoot.getString("title"));
                        root.setRate(jsonCompRoot.getInt("rate"));
                        root.setCompletedCount(jsonCompRoot.getInt("completed_count"));
                        root.setImageUrl(jsonCompRoot.getString("image_url"));
                        root.setAcceptFrag(jsonCompRoot.getInt("accept_flag"));
                        root.setClearDate(compDate[i]);

                        _compRootList.add(root);
                    }

                    // クリアしたルートの取得
                    for (int i = 0; i < postCount; i++) {
                        Root root = new Root();
                        JSONObject jsonPostRoot = jsonPostRoots.getJSONObject(i);

                        root.setTitle(jsonPostRoot.getString("title"));
                        root.setRate(jsonPostRoot.getInt("rate"));
                        root.setCompletedCount(jsonPostRoot.getInt("completed_count"));
                        root.setImageUrl(jsonPostRoot.getString("image_url"));
                        root.setAcceptFrag(jsonPostRoot.getInt("accept_flag"));

                        _postRootList.add(root);
                    }

                    compRootAdapter.addAll(_compRootList);
                    postRootAdapter.addAll(_postRootList);

                    setListViewHeightBasedOnChildren(compListView);
                    setListViewHeightBasedOnChildren(postListView);

                } catch (Exception e) {
                    System.out.println(e);
                }
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

    // リストビューの高さが一行になってしまうバグ修正の為のコード
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
