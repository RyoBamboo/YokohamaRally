package com.dr.yokohamarally.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.activities.MainActivity;
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
    private int count;

    @Override
    public void onCreate() {
        Toast.makeText(this, "バックグラウンドサービスを開始しました。", Toast.LENGTH_SHORT).show();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        String message = ("LocationEx>"+
                "緯度:"+location.getLatitude()+
                "経度:"+location.getLongitude());
        sendNotification(message);
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

        if(number!=0)return;
        mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();
        Intent intent = new Intent( GpsService.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        n.icon = R.drawable.ic_launcher;
        n.tickerText = "YOKOHAMA スタンプラリー";
        n.number = number;

        n.setLatestEventInfo(getApplicationContext(), "YOKOHAMA スタンプラリー"+ count, message, pi);

        mManager.notify(number, n);
        number++;
    }

}