package com.dr.yokohamarally.modules;

import android.content.Context;

import com.dr.yokohamarally.YokohamarallyApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class AndroidModule {

    @Provides
    @Singleton
    Context provideAppContext() {
        return YokohamarallyApplication.getInstance().getApplicationContext();
    }
}
