package com.dr.yokohamarally.services;

import com.dr.yokohamarally.cores.Envelope;
import com.dr.yokohamarally.models.Root;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface RootService {

    @GET("/root/all")
    Envelope<List<Root>> getTotalRoots(@QueryMap Map<String, String> options);

}
