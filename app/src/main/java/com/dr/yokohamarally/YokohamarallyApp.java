package com.dr.yokohamarally;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.dr.yokohamarally.core.RequestManager;

/**
 * アプリが起動したら呼ばれる初期化クラス
 */

public class YokohamarallyApp extends Application{

    // RequestQueueとImageLoaderはシングルトンで呼び出す
    private static RequestQueue sRequestQueue;
    private static ImageLoader sImageLoader;
    private static Context sContext;



        @Override
        public void onCreate() {
            super.onCreate();
            init();
        }

    private void init() {
        // Volleyの初期化
        RequestManager.init(this);
    }
}

