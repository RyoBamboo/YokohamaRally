package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;

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

                        } catch (Exception e) {

                        }

                        // トップ画像の取得
                        requestTopImage();

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
                        System.out.println(imageView.getWidth());
                    }
                },
                // 最大の幅、指定無しは0
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
