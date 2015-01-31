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

        // VolleyのRequestQueueとImageLoaderを初期化する
        sRequestQueue = Volley.newRequestQueue(getApplicationContext());
        sImageLoader  = new ImageLoader(sRequestQueue, new ImageLruCache());

        // アプリケーションコンテクストを格納する
        sContext = getApplicationContext();
    }

    /**
     * VolleyのRequestQueue を取得する
     */
    public static RequestQueue getsRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(getAppContext());
        }
        return sRequestQueue;
    }

    /**
     * VolleyのImageLoader を取得する
     */
    public static ImageLoader getsImageLoader() {
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(sRequestQueue, new ImageLruCache());
        }
        return sImageLoader;
    }

    /**
     * アプリケーションコンテクストを取得する
     */
    public static Context getAppContext() {
        return sContext;
    }

    /**
     * 画像のキャッシュを行う LruCache
     * ※ 参考: http://qiita.com/gari_jp/items/829a54bfa937f4733e29
     */
    public static class ImageLruCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mMemoryCache;

        public ImageLruCache() {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mMemoryCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mMemoryCache.put(url, bitmap);
        }
    }



}
