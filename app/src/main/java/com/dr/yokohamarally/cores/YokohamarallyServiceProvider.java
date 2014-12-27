package com.dr.yokohamarally.cores;


import retrofit.RestAdapter;

public class YokohamarallyServiceProvider {

    private RestAdapter mRestAdapter;

    public YokohamarallyServiceProvider(RestAdapter restAdapter) {
        mRestAdapter = restAdapter;
    }

    public YokohamarallyService getService() {
        return new YokohamarallyService(mRestAdapter);
    }
}
