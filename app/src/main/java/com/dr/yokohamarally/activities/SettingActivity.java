package com.dr.yokohamarally.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputFilter;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.filters.EmailFilter;
import com.dr.yokohamarally.fragments.BitmapHolder;
import com.dr.yokohamarally.fragments.GpsService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingActivity extends ActionBarActivity {

    private static final int IMAGE_CAPTURE = 101;
    private static int GETTRIM = 10;
    private Bitmap bmp;
    private String bmpString;
    private InputFilter[] filter = {new EmailFilter()};
    private ActionBarDrawerToggle mDrawerToggle;

    @InjectView(R.id.name)
    EditText nameEditText;

    @InjectView(R.id.email)
    EditText emailEditText;

    @InjectView(R.id.profile)
    ImageView profileImageView;

    @InjectView(R.id.notice_flag)
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.inject(this);
        emailEditText.setFilters(filter);


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
        String[] members = { "トップページ", "マイぺージ",  "マイラリー投稿","挑戦中ページ","ログアウト" };

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
                    Intent intent = new Intent(SettingActivity.this, MyPageActivity.class);
                    startActivity(intent);

                }else if("トップページ".equals(item)){
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if("挑戦中ページ".equals(item)){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    int tryId = sp.getInt("rootId", 0);
                    if(tryId != 0 ){
                        Intent intent = new Intent(SettingActivity.this, TryActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "現在挑戦していません", Toast.LENGTH_SHORT).show();
                    }
                }else if("マイラリー投稿".equals(item)){
                    Intent intent = new Intent(SettingActivity.this, FormActivity.class);
                    intent.putExtra("remove",1 );
                    startActivity(intent);
                }else if("ログアウト".equals(item)){
                    Logout();
                }
            }
        });

        setup();
    }

    protected void setup() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String name  = sp.getString("name", "");
        String email = sp.getString("email", "");
        bmpString = sp.getString("profile_image", "");
        String noticeFlag = sp.getString("notice_flag", "");


        byte[] b = Base64.decode(bmpString, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        if (noticeFlag.equals("1")) {
            checkBox.setChecked(true);
        }

        profileImageView.setImageBitmap(bmp);
        nameEditText.setText(name);
        emailEditText.setText(email);
    }

    @OnClick(R.id.submit)
    protected void submit() {

        /*-------------------------------
         * バリデーション
         *-----------------------------*/
        String _name = nameEditText.getText().toString();
        String _email = emailEditText.getText().toString();

        if (_name.length() == 0) {Toast.makeText(this, "名前を入力してください", Toast.LENGTH_LONG).show(); return;}
        if (_email.length() == 0) {Toast.makeText(this, "メールアドレスを入力してください", Toast.LENGTH_LONG).show(); return;}

        // queue
        RequestQueue myQueue = Volley.newRequestQueue(this);

        // url
        String url = "http://yokohamarally.prodrb.com/api/update_userinfo.php";

        // Request
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println(s);
                        if (s.equals("OK")) {
                            System.out.println("OKOKOK");
                            // 更新成功したらユーザ情報をsharedPrefereceに保存
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            EditText emailEditText = (EditText) findViewById(R.id.email);
                            EditText nameEditText = (EditText) findViewById(R.id.name);
                            CheckBox checkBox = (CheckBox) findViewById(R.id.notice_flag);
                            if (checkBox.isChecked()) {
                                sp.edit().putString("notice_flag", "1").commit();
                            } else {
                                stopService(new Intent(getBaseContext(), GpsService.class));
                                sp.edit().putString("notice_flag", "0").commit();
                            }

                            sp.edit().putString("profile_image", bmpString).commit();
                            sp.edit().putString("email", emailEditText.getText().toString()).commit();
                            sp.edit().putString("name", nameEditText.getText().toString()).commit();

                            Toast.makeText(getBaseContext(), "設定を保存しました", Toast.LENGTH_LONG).show();
                        } else {
                            System.out.println(s);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                EditText emailEditText = (EditText) findViewById(R.id.email);
                EditText nameEditText = (EditText) findViewById(R.id.name);
                CheckBox checkBox = (CheckBox) findViewById(R.id.notice_flag);
                String notice_flag = "1";
                if (checkBox.isChecked()) {
                    notice_flag = "1";
                } else {
                    notice_flag = "0";
                }

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                params.put("notice_flag", notice_flag);
                params.put("profile_image", bmpString);
                params.put("id", sp.getString("id", ""));
                params.put("email", emailEditText.getText().toString());
                params.put("name", nameEditText.getText().toString());

                return params;
            }
        };

        myQueue.add(postRequest);
        myQueue.start();

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

    @OnClick(R.id.cancel)
    protected void cancel() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.picture)
    protected void uploadPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    /*-----------------------------------------
     * 画像アップロード処理
     *---------------------------------------*/
    // ギャラ裏ーから戻ってきたときの処理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(in);
                in.close();

                Intent _intent = new Intent(getApplicationContext(),TrimingActivity.class);
                BitmapHolder._holdedBitmap = bmp;
                startActivityForResult(_intent,GETTRIM);

            } catch (Exception e) {

            }
        } else if (requestCode == GETTRIM) {
            ImageView profile = (ImageView)findViewById(R.id.profile);
            profile.setImageBitmap(BitmapHolder._holdedBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapHolder._holdedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bmpString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }
    }

    public void Logout() {
        new AlertDialog.Builder(SettingActivity.this)
                .setTitle("ログアウトしますか？")
                .setPositiveButton(
                        "はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ログアウトしてログインページへ
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                sp.edit().putBoolean("isLogin", false).commit();
                                Intent intent = new Intent(SettingActivity.this, LoginActiviry.class);
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

}
