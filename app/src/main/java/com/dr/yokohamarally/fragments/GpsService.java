package com.dr.yokohamarally.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.activities.MainActivity;
import com.dr.yokohamarally.activities.TryActivity;
import com.dr.yokohamarally.tests.tets;

public class GpsService extends Service implements LocationListener {

    static final String TAG = "ExampleService";
    private LocationManager mLocationManager;
    private NotificationManager mManager;
    private int number = 0;
    private final static String BR=System.getProperty("line.separator");
    private final static int WC= LinearLayout.LayoutParams.WRAP_CONTENT;
    private TextView textView;       //テキストビュー
    private LocationManager locationManager;//ロケーションマネージャ
    private String[] latitude;
    private String[] longitude;
    private String[] title;
    private double[] dLonguitude;
    private double[] dLatitude;
    private int pointNum;
    private int[] count;

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        latitude = getArrayFromSharedPreference("tryLatitude");
        longitude = getArrayFromSharedPreference("tryLongitude");
        title = getArrayFromSharedPreference("tryTitle");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        pointNum = sp.getInt("checkpointNum", 0);

        dLatitude = new double[pointNum+1];
        dLonguitude = new double[pointNum+1];
        count = new int[pointNum+1];
        for(int i = 0 ; i < pointNum; i++){
            dLatitude[i] = Double.parseDouble(latitude[i]);
            dLonguitude[i] = Double.parseDouble(longitude[i]);
            count[i] = 0;

        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        //明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mLocationManager.removeUpdates(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        for(int i=0 ; i < pointNum ; i++) {
            System.out.println(i  +" " +( location.getLatitude() - dLatitude[i]));
            if (Math.abs(location.getLatitude() - dLatitude[i]) < 0.003 && Math.abs(location.getLongitude() - dLonguitude[i]) < 0.003){
                count[i]++;
                if(count[i] == 1) {
                    String message = title[i] + "付近に到着しました";
                    sendNotification(message);
                }


            }
        }


    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void sendNotification(String message) {

        mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();
        Intent intent = new Intent( GpsService.this, TryActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        n.icon = R.drawable.ic_launcher;
        n.tickerText = "YOKOHAMA スタンプラリー";
        n.number = number;

        n.setLatestEventInfo(getApplicationContext(), "YOKOHAMA スタンプラリー", message, pi);

        mManager.notify(number, n);
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