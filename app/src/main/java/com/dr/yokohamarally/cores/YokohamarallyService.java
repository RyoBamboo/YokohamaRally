package com.dr.yokohamarally.cores;

import com.dr.yokohamarally.models.Root;
import com.dr.yokohamarally.services.RootService;

import java.util.HashMap;
import java.util.List;

import retrofit.RestAdapter;

public class YokohamarallyService {

    private RestAdapter mRestAdapter;

    public YokohamarallyService() {
    }

    public YokohamarallyService(RestAdapter restAdapter) {
        mRestAdapter = restAdapter;
    }

    private RootService getRootService() {
        return getRestAdapter().create(RootService.class);
    }

    private RestAdapter getRestAdapter() {
        return mRestAdapter;
    }


    public Envelope<List<Root>> getTotalRoots() {
        return getRootService().getTotalRoots(new HashMap<String, String>());
    }

    public Envelope<List<Root>> getTotalRoots(HashMap<String, String> options) {
        return getRootService().getTotalRoots(options);
    }

}
