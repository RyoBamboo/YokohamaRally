package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

import com.dr.yokohamarally.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        Handler hdl = new Handler();
        hdl.postDelayed(new splashHandler(), 900);

        
    }

    class splashHandler implements Runnable {
        public void run() {

             /*------------------------
         * ログインしているか確認する
         * 現在は強制的にLoginActivityへ
        /*----------------------*/
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            if (LoginActiviry.isLogin(sp) == false) {
                Intent intent = new Intent(getApplication(), LoginActiviry.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }else {
                Intent i = new Intent(getApplication(), MainActivity.class);
                startActivity(i);
                SplashActivity.this.finish();
            }
        }
    }
}