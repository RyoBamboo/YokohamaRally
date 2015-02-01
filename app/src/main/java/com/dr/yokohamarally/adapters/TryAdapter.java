package com.dr.yokohamarally.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.R;
import com.dr.yokohamarally.activities.CameraActivity;
import com.dr.yokohamarally.models.Root;

import java.io.InterruptedIOException;
import java.util.ArrayList;

public class TryAdapter extends ArrayAdapter<Root>  {

    private LocationManager mLocal;

    // ビューを動的に書き換えるインフレイター
    private ProgressDialog progressDialog;
    private LayoutInflater layoutInflater;
    private double latitude;
    private  double longitude;
    private String[]  checkedPoints = new String[10];
    private String[]  checkedPointImages = new String[10];

    private String[] tryLatitude;
    private String[] tryLongitude;
    private double[] dLongitude;
    private double[] dLatitude;
    private int pointNum;
    private Thread thread;


    public TryAdapter(Context context, int resource, ArrayList<Root> roots)  {

        // ArrayAdapterのコンストラクタ
        super(context, resource, roots);

        // インフレイターの取得
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    // 各行に表示するViewを返すメソッド
    public View getView(int position, View convertView, ViewGroup parent) {

        tryLatitude = getArrayFromSharedPreference("tryLatitude");
        tryLongitude = getArrayFromSharedPreference("tryLongitude");



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        pointNum = sp.getInt("checkpointNum", 0);

        mLocal = (LocationManager)getContext().getSystemService(Service.LOCATION_SERVICE);



        dLatitude = new double[pointNum+1];
        dLongitude = new double[pointNum+1];




        for(int i = 0 ; i < pointNum; i++){
            Log.d("aa" , dLatitude.length +"");

            dLatitude[i] = Double.parseDouble(tryLatitude[i]);
            dLongitude[i] = Double.parseDouble(tryLongitude[i]);
        }

        // MEMO by take: リストのリロード機能実装しないので必要ない可能性あり
        if (convertView == null) {
            // Viewがまだ作成していなければ、Viewを生成＝＞View
            convertView = layoutInflater.inflate(R.layout.try_list_item, null);
        }

        String[] checkedPointsCopy = getArrayFromSharedPreference("checkedPoints");
        String[] checkedPointImagesCopy = getArrayFromSharedPreference("checkedPointImages");

        //配列拡張のための処理
        checkedPointImages = new String[10];
        checkedPoints = new String[10];
        if(checkedPointImagesCopy != null) {
            for (int i = 0; i < checkedPointImagesCopy.length; i++)
                checkedPointImages[i] = checkedPointImagesCopy[i];
        }
        if(checkedPointsCopy != null){
            for( int i=0; i<checkedPointsCopy.length; i++)checkedPoints[i]=checkedPointsCopy[i];
        }


        // 対応する行のオブジェクトを取得
        Root root = (Root)getItem(position);

        final int id = root.getId();

        convertView.setBackgroundResource(R.drawable.round_corner_list);

        // タイトルをセット
        TextView title = (TextView)convertView.findViewById(R.id.try_title);
        title.setText(root.getTitle());

        Button pictureButton = (Button)convertView.findViewById(R.id.check_button);
        pictureButton.setText("スポット到着");

        ImageView image = (ImageView)convertView.findViewById(R.id.try_check);
        if(root.getCheckedPoint()==true){
            image.setImageResource(R.drawable.checked);
        }else{
            image.setImageResource(R.drawable.unchecked);
        }

        if ( pictureButton.getTag() == null ) {

            pictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // GPS機能設定の状態を取得
                    String GpsStatus = android.provider.Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

                    if (GpsStatus.indexOf("gps", 0) < 0) {

                        //GPSが無効だった場合
                        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setMessage("GPS機能がOFFになっています。\n設定画面を開きますか？");
                        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //設定画面を呼び出す
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                getContext().startActivity(intent);

                            }
                        });
                        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //何もしない
                            }
                        });
                        ad.create();
                        ad.show();
                    } else {





                        // ロケーションマネージャの取得
                        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        // 最適な位置情報プロバイダの選択
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
                        // Criteriaを変更することで，各種設定変更可能
                        String bs = lm.getBestProvider(criteria, true);

                        Location locate = lm.getLastKnownLocation(bs);

                        locate = null;

                        int i =0;

                        while(locate == null && i < 300) {

//                            if (locate == null) {
//                                // 現在地が取得できなかった場合，GPSで取得してみる
//                                locate = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            }
//                            if (locate == null) {
//                                // 現在地が取得できなかった場合，無線測位で取得してみる
//                                locate = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                            }

                            locate = requestLocation();

                            i ++;
                            if (locate != null) { // 現在地情報取得成功
                                // 緯度の取得
                                latitude = (locate.getLatitude());
                                // 経度の取得
                                longitude = (locate.getLongitude());
                            }

                        }

                        Log.d("1",""+( dLatitude[id]));
                        Log.d("2",""+( latitude));
                        Log.d("3",""+( dLongitude[id]));
                        Log.d("4",""+( longitude));

                        System.out.println(locate);
                            //到着判定
                            if (Math.abs(latitude - dLatitude[id]) < 0.0032 && Math.abs(longitude - dLongitude[id]) < 0.0032) {

                                Log.d("1",""+( dLatitude[id]));
                                Log.d("2",""+( latitude));
                                // 到達をtrueにする
                                checkedPoints[id] = "true";
                                saveArrayToSharedPreference(checkedPoints, "checkedPoints");


                                // 全てのルートをクリアしたかチェック
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                                if (isAllCompleted() == true) {
                                    System.out.println("completed");
                                    sp.edit().putBoolean("isCompleted", true).commit();
                                } else {
                                    sp.edit().putBoolean("isCompleted", false).commit();
                                    System.out.println("No completed");
                                }

                                Intent intent = new Intent(getContext(), CameraActivity.class);
                                intent.putExtra("reachingNumber", id);
                                getContext().startActivity(intent);


                            } else {
                                Log.d("1",""+( dLatitude[id]));
                                Log.d("2",""+( latitude));
                                Log.d("3",""+( dLongitude[id]));
                                Log.d("4",""+( longitude));

                                if(i ==300){
                                    Toast.makeText(getContext(), "現在地を取得できませんでした。\nしばらくしてもう一度試してください", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "まだ到着していません ", Toast.LENGTH_SHORT).show();
                                }

                            }



                    }
                }
            });
        }





        /*------------
         * 画像取得
         *----------*/
        String imageUrl = root.getImageUrl();


        final ImageView imageView = (ImageView)convertView.findViewById(R.id.try_image);

        if("true".equals(checkedPoints[id])){
            byte[] b = Base64.decode(checkedPointImages[id], Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(bmp);
        }else {

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

                        }
                    }
            );

            RequestQueue myQueue;
            myQueue = Volley.newRequestQueue(this.getContext());

            myQueue.add(request);
            myQueue.start();
        }

        return convertView;
    }

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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            sp.edit().putString(key, stringItem).commit();
        }
    }

    public String[] getArrayFromSharedPreference(String key) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String stringItem = sp.getString(key, "");
        if (stringItem != null && stringItem.length() != 0) {
            return  stringItem.split(",");
        }

        return null;
    }


    // TODO: デバッグの為、強制的にtrueを返す
    // 全てのルートをクリアしたか判定する関数
    protected boolean isAllCompleted() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] completedPoint = getArrayFromSharedPreference("checkedPoints");
        int checkpointNum = sp.getInt("checkpointNum", 0); // 全チェックポイント数
        int compeletedCount = 0; // クリアしたチェックポイント数


        for (String value : completedPoint) {
            if (value.equals("true")) {
                compeletedCount ++;
            }
        }

        System.out.println("check=" + checkpointNum);
        System.out.println("comp=" + compeletedCount);

        if (checkpointNum == compeletedCount) {
            return true;
        }

        return false;
    }


    private Location requestLocation(){

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        mLocal.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0,
                locationListener);
        Location location = mLocal.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }





}
