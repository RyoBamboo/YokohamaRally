package com.dr.yokohamarally.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.ImagePopup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapActivity extends Activity {

    // GoogleMapオブジェクトの宣言
    private GoogleMap googleMap;
    double latitude, longitude,min_x,max_x,min_y,max_y,middle_x,middle_y,sub_x,sub_y,sub_max;
    public int rootId;

    /*-------------------------
     * 概要ページ用変数
     *-----------------------*/
    private String rootTitle;
    private int    rootRate;
    private String rootSummary;
    private String imageUrl;
    private double[] pointLatitude;
    private String[] pointImageTitle;
    private double[] pointLongitude;
    private String[] pointImageUrls;


    private RequestQueue myQueue;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_main);

        Intent intent = getIntent();
        rootId = intent.getIntExtra("rootId", 0);

        //横浜駅の座標取得
        longitude = 35.466188;
        latitude = 139.622715;

        min_x = 140.0;
        max_x = 120.0;
        min_y = 40.0;
        max_y = 10.0;

        myQueue = Volley.newRequestQueue(this);

        String url = "http://yokohamarally.prodrb.com/api/get_root_by_id.php?id=";
        String params = String.valueOf(rootId);
        StringBuffer buf = new StringBuffer();
        buf.append(url);
        buf.append(params);
        String uri = buf.toString();
        System.out.println(uri);

        myQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {

                            JSONArray json_points = response.getJSONArray("points");
                            pointImageUrls = new String[json_points.length()]; // ポイントの画像URLを保存する配列
                            pointImageTitle = new String[json_points.length()]; // ポイントの画像タイトルを保存する配列
                            pointLongitude = new double[json_points.length()]; // ポイントの緯度を保存する配列
                            pointLatitude = new double[json_points.length()]; // ポイントの経度を保存する配列

                            for (int i = 0; i < json_points.length(); i++) {
                                JSONObject json_point = json_points.getJSONObject(i);
                                System.out.println(json_point);
                                pointImageUrls[i] = json_point.getString("image_url");
                                pointImageTitle[i] = json_point.getString("name");
                                pointLatitude[i] = json_point.getDouble("latitude");
                                pointLongitude[i] = json_point.getDouble("longitude");

                            }

                            // adapterに反映、追加
                        } catch (Exception e) {
                            System.out.println(e);

                        }

                        for (int i = 0; i < pointImageTitle.length; i++) {
                            makePin(pointLongitude[i], pointLatitude[i], pointImageTitle[i], i);
                            Log.d("map", pointLongitude[i] + "this");
                        }

                        // MapFragmentオブジェクトを取得
                        MapFragment mapFragment = (MapFragment) getFragmentManager()
                                .findFragmentById(R.id.map);
                        // GoogleMapオブジェクトの取得
                        googleMap = mapFragment.getMap();

                        for(int i= 0; i < pointImageUrls.length ; i++ ){
                            if(pointLatitude[i] > max_x  )max_x = pointLatitude[i];
                            if(pointLatitude[i] < min_x  )min_x = pointLatitude[i];
                            if(pointLongitude[i] > max_y  )max_y = pointLongitude[i];
                            if(pointLongitude[i] < min_y  )min_y = pointLongitude[i];
                        }

                        mapInit();



                        if (googleMap != null) {
                            // InfoWindowAdapter設定
                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoContents(Marker marker) {
                                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                                    // タイトル設定
                                    TextView title = (TextView)view.findViewById(R.id.info_title);
                                    title.setText(marker.getTitle());
                                    // 画像設定

                                    final String url = "http://yokohamarally.prodrb.com/img/" + pointImageUrls[0];
                                    final ImageView imageView = (ImageView)view.findViewById(R.id.info_image);

                                    ImageRequest request = new ImageRequest(
                                            url,
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap response) {
                                                    imageView.setImageBitmap(response);
                                                    System.out.println(response);
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

                                    myQueue.add(request);
                                    myQueue.start();


                                    return view;
                                }

                                @Override
                                public View getInfoWindow(Marker marker) {
                                    // TODO Auto-generated method stub
                                    return null;
                                }
                            });
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //エラー時の処理
                        System.out.println(error);

                    }

        }));

        // MapFragmentオブジェクトを取得
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        // GoogleMapオブジェクトの取得
        googleMap = mapFragment.getMap();


        // Activityが初回で生成されたとき
        if (savedInstanceState == null) {

            // MapFragmentのオブジェクトをセット
            mapFragment.setRetainInstance(true);

            // 地図の初期設定を行うメソッドの呼び出し
            mapInit();
        }
    }


    // 地図の初期設定メソッド
    private void mapInit() {

        // 地図タイプ設定
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        googleMap.setMyLocationEnabled(true);






        //////中心とるための準備

        middle_x = (max_x + min_x ) / 2;
        middle_y = (max_y + min_y ) / 2;
        sub_x = max_x - min_x;
        sub_y = max_y - min_y;

        if( sub_x  <= sub_y * 2 )sub_max = sub_y * 2;
        else if( sub_x  > sub_y*2 )sub_max = sub_x;

        if(max_y == min_y && max_x == min_x)sub_max = 0.39;

        System.out.println((float)(sub_max*3));


        // カメラ位置設定
         CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(middle_y, middle_x)).zoom((float)(sub_max*36)).build();

        // 地図の中心の変更する
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));






    }


    public void makePin(double x,double y,String title , int i){

        // オプション設定
        MarkerOptions options = new MarkerOptions();
        // 緯度・経度
        options.position(new LatLng(x, y));
        // タイトル・スニペット
        options.title(title);
        // マーカーを貼り付け
        googleMap.addMarker(options);



    }



    private void requestPointImage(int i, View view) {

    }



}
