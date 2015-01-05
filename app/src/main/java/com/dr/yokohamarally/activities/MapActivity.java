package com.dr.yokohamarally.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.dr.yokohamarally.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

    // GoogleMapオブジェクトの宣言
    private GoogleMap googleMap;
    double latitude, longitude;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_main);

        // MapFragmentオブジェクトを取得
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        try {
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
        // GoogleMapが使用不可のときのためにtry catchで囲っています。
        catch (Exception e) {
        }
    }

    // 地図の初期設定メソッド
    private void mapInit() {

        // 地図タイプ設定
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        googleMap.setMyLocationEnabled(true);


        //横浜駅の座標取得
        longitude = 35.466188;
        latitude = 139.622715;


        // カメラ位置設定
        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(longitude, latitude)).zoom(13f).build();

        // 地図の中心の変更する
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));

        makePin(longitude,latitude,"横浜駅","第一スポット");

    }


    public void makePin(double x,double y,String title , String snippet){

        // オプション設定
        MarkerOptions options = new MarkerOptions();
        // 緯度・経度
        options.position(new LatLng(x, y));
        // タイトル・スニペット
        options.title(title);
        options.snippet(snippet);
        // マーカーを貼り付け
        googleMap.addMarker(options);


    }

}
