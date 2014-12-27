package com.dr.yokohamarally.modules;

import android.content.Context;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.YokohamarallyApplication;
import com.dr.yokohamarally.activities.MainActivity;
import com.dr.yokohamarally.cores.RestErrorHandler;
import com.dr.yokohamarally.cores.YokohamarallyService;
import com.dr.yokohamarally.cores.YokohamarallyServiceProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = false,
        injects =  {
                YokohamarallyApplication.class,
                MainActivity.class
        }
)

public class YokohamarallyModule {

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides
    YokohamarallyService provideYokohamarallyService(RestAdapter restAdapter) {
        return new YokohamarallyService(restAdapter);
    }

    @Provides
    YokohamarallyServiceProvider provideYokohamarallyServiceProvider(RestAdapter restAdapter) {
        return new YokohamarallyServiceProvider(restAdapter);
    }

    @Provides
    YokohamarallyApplication provideYokohamarallyApplication() {
        return YokohamarallyApplication.getInstance();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    RestAdapter provideRestAdapter(Context context, RestErrorHandler restErrorHandler) {
        return new RestAdapter.Builder()
                .setEndpoint(context.getString(R.string.constants_api_endpoint))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(restErrorHandler)
                //.setConverter(new GsonConverter(gson))
                .build();
    }

}
