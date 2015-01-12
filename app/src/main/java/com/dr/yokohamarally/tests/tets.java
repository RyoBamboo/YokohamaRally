package com.dr.yokohamarally.tests;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.activities.MainActivity;
import com.dr.yokohamarally.activities.RootSummaryActivity;
import com.dr.yokohamarally.fragments.GpsService;

//位置情報の取得
public class tets extends Activity
        implements LocationListener {

    private NotificationManager mManager;
    private int number = 0;
    private final static String BR=System.getProperty("line.separator");
    private final static int WC=LinearLayout.LayoutParams.WRAP_CONTENT;
    private TextView        textView;       //テキストビュー
    private LocationManager locationManager;//ロケーションマネージャ
    private int count;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        LinearLayout layout=new LinearLayout(this);
        layout.setBackgroundColor(Color.WHITE);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        //テキストビューの生成
        textView=new TextView(this);
        textView.setText("LocationEx");
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        layout.addView(textView);

        count = 0;


    }

    //アクティビティ開始時に呼ばれる
    @Override
    public void onStart() {
        super.onStart();
        //ロケーションマネージャの設定
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
    }

    //アクティビティ停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();

        //ロケーションマネージャーの設定
        locationManager.removeUpdates(this);
    }

    //位置情報変更のイベント処理
    public void onLocationChanged(Location location) {
        //緯度と経度の取得
        textView.setText("LocationEx>"+BR+
                "緯度:"+location.getLatitude()+BR+
                "経度:"+location.getLongitude());

        //通知送る
        sendNotification();
    }

    //位置情報取得有効化のイベントの処理
    public void onProviderEnabled(String provider) {
    }

    //位置情報取得無効化のイベント処理
    public void onProviderDisabled(String provider) {
    }

    //位置情報状態変更のイベント処理
    public void onStatusChanged(String provider,
                                int status,Bundle extras) {
    }



    private void sendNotification() {
        mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();
        Intent intent = new Intent(tets.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        n.icon = R.drawable.ic_launcher;
        n.tickerText = "YOKOHAMA スタンプラリー";
        n.number = number;

        n.setLatestEventInfo(getApplicationContext(), "YOKOHAMA スタンプラリー"+ count, "挑戦中のスポットに到着しました", pi);

        mManager.notify(number, n);
        number++;
        count++;
    }
}