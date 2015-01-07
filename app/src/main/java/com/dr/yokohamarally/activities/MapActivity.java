package com.dr.yokohamarally.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dr.yokohamarally.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

    // GoogleMapオブジェクトの宣言
    private GoogleMap googleMap;
    double latitude, longitude,min_x,max_x,min_y,max_y,middle_x,middle_y,sub_x,sub_y;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_main);

        //横浜駅の座標取得
        longitude = 35.466188;
        latitude = 139.622715;


        // MapFragmentオブジェクトを取得
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        // GoogleMapオブジェクトの取得
        googleMap = mapFragment.getMap();

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
                    ImageView img = (ImageView)view.findViewById(R.id.info_image);
                    img.setImageResource(R.drawable.ookami);
                    return view;
                }

                @Override
                public View getInfoWindow(Marker marker) {
                    // TODO Auto-generated method stub
                    return null;
                }
            });
        }
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





        // カメラ位置設定
        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(longitude, latitude)).zoom(13f).build();

        // 地図の中心の変更する
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));



/*//////中心とるための準備

//        min_x = 120.0;
//        max_x = 140.0;
//        min_y = 10.0;
//        max_y = 40.0;
//
//        for(int i= 0; i < vi.length + 1 ; i++ ){
//            if(vi[i] > max_x  )max_x = vi[i];
//            if(vi[i] > min_x  )min_x = vi[i];
//            if(vi[i] > max_y  )max_y = vi[i];
//            if(vi[i] > min_y  )min_y = vi[i];
//        }
//
//        middle_x = (max_x + min_x ) / 2;
//        middle_y = (max_y + min_y ) / 2;
//        sub_x = max_x - min_x;
//        sub_y = max_y - min_y;
//
//
//        // カメラ位置設定
//        CameraPosition camerapos = new CameraPosition.Builder()
//                .target(new LatLng(middle_x, middle_y)).zoom(13f).build();
//
//        // 地図の中心の変更する
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
*/


        makePin(longitude,latitude,"横浜駅","第一スポット");
        makePin(longitude+1,latitude,"横駅","第一スポット");
        makePin(longitude+2,latitude,"浜駅","第一スポット");
        makePin(longitude+3,latitude,"横浜","第一スポット");


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
