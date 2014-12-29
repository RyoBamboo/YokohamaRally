package com.dr.yokohamarally;

import android.app.Application;

import com.dr.yokohamarally.core.RequestManager;

/**
 * アプリが起動したら呼ばれる初期化クラス
 */

public class YokohamarallyApp extends Application{

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
