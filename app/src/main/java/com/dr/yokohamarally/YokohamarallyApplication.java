package com.dr.yokohamarally;


import android.app.Application;

import com.dr.yokohamarally.cores.Injector;
import com.dr.yokohamarally.modules.BaseModule;

public class YokohamarallyApplication extends Application {

    private static YokohamarallyApplication instance;

    public YokohamarallyApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.init(new BaseModule(), this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static YokohamarallyApplication getInstance() {
        return instance;
    }
}
