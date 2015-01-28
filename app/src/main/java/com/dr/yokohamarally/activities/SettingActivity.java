package com.dr.yokohamarally.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;

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

    @InjectView(R.id.name)
    EditText nameEditText;

    @InjectView(R.id.email)
    EditText emailEditText;

    @InjectView(R.id.profile)
    ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.inject(this);
        setup();
    }

    protected void setup() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String name  = sp.getString("name", "");
        String email = sp.getString("email", "");
        String profileImageStr = sp.getString("profile_image", "");

        byte[] b = Base64.decode(profileImageStr, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        profileImageView.setImageBitmap(bmp);
        nameEditText.setText(name);
        emailEditText.setText(email);
    }

    @OnClick(R.id.submit)
    protected void submit() {
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
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

}
