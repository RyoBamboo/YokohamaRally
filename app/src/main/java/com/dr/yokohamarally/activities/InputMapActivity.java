package com.dr.yokohamarally.activities;

import com.dr.yokohamarally.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public class InputMapActivity extends Activity {
    private GoogleMap mMap = null;
    private String adress;
    private int number;
    private int mNumber;
    private String[] pointLatitude = new String[10];
    private String[] pointLongitude = new String[10];
    private String[] pointAdress = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_main);

        Intent intent = getIntent();
        number = intent.getIntExtra("pointId" ,0);
        mNumber = intent.getIntExtra("myId" ,0);


        String[] pointLatitudeCopy = getArrayFromSharedPreference("pointLatitude");
        String[] pointLongitudeCopy = getArrayFromSharedPreference("pointLongtitude");
        String[] pointAdressCopy = getArrayFromSharedPreference("pointAdress");
        if(pointLatitudeCopy != null){
            for( int i=0; i<pointLatitudeCopy.length; i++)pointLatitude[i]=pointLatitudeCopy[i];
        }
        if(pointLongitudeCopy != null){
            for( int i=0; i<pointLongitudeCopy.length; i++)pointLongitude[i]=pointLongitudeCopy[i];
        }
        if(pointAdressCopy != null){
            for( int i=0; i<pointAdressCopy.length; i++)pointAdress[i]=pointAdressCopy[i];
        }









        // MapFragmentオブジェクトを取得
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        // GoogleMapオブジェクトの取得
        mMap = mapFragment.getMap();

        // Activityが初回で生成されたとき
        if (savedInstanceState == null) {

            // MapFragmentのオブジェクトをセット
            mapFragment.setRetainInstance(true);

            // 地図の初期設定を行うメソッドの呼び出し
            mapInit();
        }

        if (mMap != null) {
        // タップ時のイベントハンドラ登録
           mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    // 中心位置の緯度/経度取得
                    // 住所の取得
                    StringBuffer strAddr = new StringBuffer();
                    Geocoder gcoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> lstAddrs = gcoder.getFromLocation(point.latitude, point.longitude, 1);
                        pointLatitude[mNumber]=point.latitude+"";
                        pointLongitude[mNumber]=point.longitude+"";
                        for (Address addr : lstAddrs) {
                            int idx = addr.getMaxAddressLineIndex();
                            for (int i = 1; i <= idx; i++) {
                                strAddr.append(addr.getAddressLine(i));
                                adress =addr.getAddressLine(i);
                                }
                            }

                        if (adress.indexOf("横浜市") != -1) {
                            String[] adr = adress.split(" ", 0);
                            if(adr.length >=2)adress = adr[1];
                            check();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "横浜市を選択してください", Toast.LENGTH_LONG).show();
                        }

                    }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        }
                    }
                });

            // 長押し時のイベントハンドラ登録
            mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    // TODO Auto-generated method stub
                    }
                });
            }
        }
    public void check () {
        new AlertDialog.Builder(InputMapActivity.this)
                .setTitle("確認")
                .setMessage(adress+"でよろしいですか？")
                .setPositiveButton(
                        "はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pointAdress[mNumber] = adress;

                                saveArrayToSharedPreference(pointAdress, "pointAdress");
                                saveArrayToSharedPreference(pointLatitude, "pointLatitude");
                                saveArrayToSharedPreference(pointLongitude, "pointLongitude");
                                Intent intent = new Intent(InputMapActivity.this, FormActivity.class);
                                intent.putExtra("pointId", number);
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

    private void mapInit() {

        // 地図タイプ設定
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        mMap.setMyLocationEnabled(true);

        // カメラ位置設定
        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(35.466163969982134, 139.62272706994057)).zoom((float)(0.35*36)).build();

        // 地図の中心の変更する
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));



    }

}
