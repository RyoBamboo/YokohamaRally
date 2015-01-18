package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;
import com.dr.yokohamarally.fragments.ImagePopup;
import com.dr.yokohamarally.fragments.TryInformation;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONObject;

public class TryActivity extends Activity {

    private RequestQueue mQueue;

    /*-------------------------
    * 変数
    *-----------------------*/
    private int    rootId;
    private String[]  checkedPointIds;
    private int    rootRate;
    private String rootTitle;
    private String rootSummary;
    private String imageUrl;
    private double[] pointLatitude;
    private String[] pointImageTitle;
    private double[] pointLongitude;
    private String[] pointImageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);

        /*--------------------------------------------------
         * 挑戦中のルートIDと達成したチェックポイントのIDを取得
         *------------------------------------------------*/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        rootId = sp.getInt("rootId", 0);
        Log.d("MYTAG",rootId + "");

        /*--------------------------------------------------------
         * TODO: サンプルとしてチェックポイント1, 2をクリア済みに設定
         *------------------------------------------------------*/
        checkedPointIds = new String[] {"1", "2"};
        saveArrayToSharedPreference(checkedPointIds, "checkedPoints");


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
                                pointImageTitle[i] = json_point.getString("name");
                                pointLatitude[i] = json_point.getDouble("latitude");
                                pointLongitude[i] = json_point.getDouble("longitude");
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
    protected void requestCheckPoint(int i) {
        final LinearLayout parentLinearLayout = (LinearLayout)findViewById(R.id.checkpoints);
        final String url = "http://yokohamarally.prodrb.com/img/" + pointImageUrls[i];
        final ImageView mImageView = new ImageView(this);
        //写真の順番番号
        final int number = i;
        SharedPreferences check = PreferenceManager.getDefaultSharedPreferences(this);
        //写真のID
        final int pictureId = check.getInt("pictureId", -1);



        ImageRequest request = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        final LinearLayout childLinearLayout = new LinearLayout(getBaseContext());
                        childLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        mImageView.setImageBitmap(response);
                        if(TryInformation.reaching[number] == 1 )mImageView.setImageBitmap(TryInformation.holdedBitmap[number]);
                        mImageView.setPadding(0, 20, 10, 0);
                        childLinearLayout.addView(mImageView);
                        mImageView.setId(number);

                        Button pictureButton = new Button(getBaseContext());
                        pictureButton.setId(number);
                        pictureButton.setText("写真をとる");

                        pictureButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                // GPS機能設定の状態を取得
                                String GpsStatus = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

                                if (GpsStatus.indexOf("gps", 0) < 0) {

                                    //GPSが無効だった場合
                                    AlertDialog.Builder ad = new AlertDialog.Builder(TryActivity.this);
                                    ad.setMessage("GPS機能がOFFになっています。\n設定画面を開きますか？");
                                    ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //設定画面を呼び出す
                                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(intent);

                                        }
                                    });
                                    ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int whichButton) {
                                            //何もしない
                                        }
                                    });
                                    ad.create();
                                    ad.show();
                                } else {
                                    //GPSが有効だった場合

                                    // ロケーションマネージャの取得
                                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                                    // 最適な位置情報プロバイダの選択
                                    // Criteriaを変更することで，各種設定変更可能
                                    String bs = lm.getBestProvider(new Criteria(), true);

                                    Location locate = lm.getLastKnownLocation(bs);
                                    if(locate == null){
                                        // 現在地が取得できなかった場合，GPSで取得してみる
                                        locate = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    }
                                    if(locate == null){
                                        // 現在地が取得できなかった場合，無線測位で取得してみる
                                        locate = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    }
                                    if(locate != null){ // 現在地情報取得成功
                                        // 緯度の取得
                                        double latitude=(locate.getLatitude());
                                        // 経度の取得
                                        double longitude=(locate.getLongitude());
                                        Log.d("MYTAG", String.valueOf(latitude));
                                        Log.d("MYTAG",String.valueOf(longitude));
                                    }
                                    if(locate == null){
                                        Log.d("MYTAG","no!");

                                    }


                                    // 到達をtrueにする
                                    TryInformation.reaching[number] = 1;
                                    Intent intent = new Intent(TryActivity.this, CameraActivity.class);
                                    intent.putExtra("reachingNumber", number);
                                    startActivity(intent);
                                }





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


    /*---------------------------------------------------------
     * SharedPreferenceに配列の保存と読み込みを可能にする関数。
     * TODO: できれば他のクラスからも参照できるようにリファクタリングしたい
     *-------------------------------------------------------*/
    public void saveArrayToSharedPreference(String[] array, String key) {

        // ','をキーとして連結
        StringBuffer buffer = new StringBuffer();
        for (String value : array) {
            buffer.append(value + ",");
        }

        String stringItem = null;
        if (buffer != null) {
            String buf = buffer.toString();
            stringItem = buf.substring(0, buf.length() - 1);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putString(key, stringItem).commit();
        }
    }

    public String[] getArrayFromSharedPreference(String key) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String stringItem = sp.getString(key, "");
        if (stringItem != null && stringItem.length() != 0) {
            return  stringItem.split(",");
        }

        return null;
    }

}
