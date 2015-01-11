package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.ImagePopup;

import org.json.JSONArray;
import org.json.JSONObject;

public class TryActivity extends Activity {

    private RequestQueue mQueue;

    /*-------------------------
    * 概要ページ用変数
    *-----------------------*/
    private String rootTitle;
    private int    rootRate;
    private String rootSummary;
    private String imageUrl;
    private String[] pointImageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);

        /*--------------------------------
         * サンプルのため、ここでデータを格納
         *------------------------------*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("rootId", 1).commit(); // 挑戦中のルートID


        /*--------------------------------
         * 挑戦中のルート情報をIDから取得
         *------------------------------*/
        mQueue = Volley.newRequestQueue(this);
        String url = "http://yokohamarally.prodrb.com/api/get_root_by_id.php?id=";
        String params = String.valueOf(sp.getInt("rootId", 0));
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();

        mQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray json_roots = response.getJSONArray("roots");

                            for(int i = 0; i < json_roots.length(); i++) {
                                JSONObject json_root = json_roots.getJSONObject(i);

                                rootTitle = json_root.getString("title");
                                rootSummary = json_root.getString("summary");
                                rootRate = json_root.getInt("rate");
                                System.out.println(rootRate);
                                imageUrl = "http://yokohamarally.prodrb.com/img/" + json_root.getString("image_url");
                            }

                            JSONArray json_points = response.getJSONArray("points");
                            pointImageUrls = new String[json_points.length()]; // ポイントの画像URLを保存する配列
                            for (int i = 0; i < json_points.length(); i++) {
                                JSONObject json_point = json_points.getJSONObject(i);
                                pointImageUrls[i] = json_point.getString("image_url");
                            }

                        } catch (Exception e) {

                        }

                        // トップ画像の取得
                        requestTopImage();

                        // チェックポイントの表示
                        for (int i = 0; i < pointImageUrls.length; i++) {
                            requestCheckPoint(i);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ));

    }

    private void requestTopImage() {
        // ImageViewの取得
        final ImageView imageView = (ImageView)findViewById(R.id.root_image);

        ImageRequest request = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                },
                // 最大の幅、指定無しは0
                0,
                0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }
        );

        mQueue.add(request);
        mQueue.start();
    }


    // TODO: かなり無理やりリファクタリング必須
    private void requestCheckPoint(int i) {
        final LinearLayout parentLinearLayout = (LinearLayout)findViewById(R.id.checkpoints);
        final String url = "http://yokohamarally.prodrb.com/img/" + pointImageUrls[i];
        final ImageView mImageView = new ImageView(this);

        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        final LinearLayout childLinearLayout = new LinearLayout(getBaseContext());
                        childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mImageView.setImageBitmap(response);
                        mImageView.setPadding(0, 20, 10, 0);
                        childLinearLayout.addView(mImageView);
                        Button pictureButton = new Button(getBaseContext());
                        pictureButton.setText("写真をとる");

                        pictureButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TryActivity.this, CameraActivity.class);
                                startActivity(intent);

                            }
                        });


                        childLinearLayout.addView(pictureButton);
                        parentLinearLayout.addView(childLinearLayout);
                    }
                },
                0,
                0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        mQueue.add(request);
        mQueue.start();
    }

}
