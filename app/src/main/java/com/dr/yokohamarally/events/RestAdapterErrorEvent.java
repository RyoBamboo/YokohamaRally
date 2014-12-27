package com.dr.yokohamarally.events;

import retrofit.RetrofitError;

public class RestAdapterErrorEvent {

    private RetrofitError mRetrofitError;

    public RestAdapterErrorEvent(RetrofitError cause) {
        mRetrofitError = cause;
    }

    public RetrofitError getRetrofitError() {
        return mRetrofitError;
    }
}
