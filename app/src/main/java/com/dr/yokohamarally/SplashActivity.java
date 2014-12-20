package com.dr.yokohamarally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        Handler hdl = new Handler();
        hdl.postDelayed(new splashHandler(), 500);

        
    }

    class splashHandler implements Runnable {
        public void run() {
            Intent i = new Intent(getApplication(),
                    MainActivity3.class);
            startActivity(i);
            SplashActivity.this.finish();
        }
    }
}