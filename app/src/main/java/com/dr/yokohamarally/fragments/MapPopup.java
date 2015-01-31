package com.dr.yokohamarally.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapPopup extends DialogFragment {

    String mUrl;
    private GoogleMap mMap = null;
    int mid;
    private RequestQueue myQueue;
    private String mTitle;
    private String mSummary;
    private Dialog dialog;
    private double mlatitude;
    private double mlongitude;
    private String mtitle;


    public static MapPopup newInstance( String title ,double latitude,double longitude){


        MapPopup instance = new MapPopup();
        Bundle bundle = new Bundle();
        bundle.putString("title" , title);
        bundle.putDouble("latitude" , latitude);
        bundle.putDouble("longitude" , longitude);

        instance.setArguments(bundle);

        return instance;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mtitle = getArguments().getString("title");
        mlongitude = getArguments().getDouble("longitude");
        mlatitude = getArguments().getDouble("latitude");


        dialog = new Dialog(getActivity());




        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.dialog_map);

        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // MapFragmentオブジェクトを取得
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
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

        LatLng position = new LatLng(mlatitude, mlongitude);
        MarkerOptions options = new MarkerOptions();
        options.position(position);
        options.title(mtitle);
        mMap.addMarker(options);


        // OK ボタンのリスナ
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction().remove(mapFragment).commit();
                dismiss();
            }
        });
        // Close ボタンのリスナ
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(mapFragment).commit();

                dismiss();

            }
        });




        return dialog;
    }

    private void mapInit() {

        // 地図タイプ設定
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示を行なう
        mMap.setMyLocationEnabled(true);

        // カメラ位置設定
        CameraPosition camerapos = new CameraPosition.Builder()
                .target(new LatLng(mlatitude, mlongitude)).zoom((float)(0.35*36)).build();

        // 地図の中心の変更する
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));



    }

}