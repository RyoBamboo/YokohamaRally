package com.dr.yokohamarally.cores;

import com.dr.yokohamarally.events.RestAdapterErrorEvent;
import com.squareup.otto.Bus;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RestErrorHandler implements ErrorHandler {

    private Bus mBus;

    public RestErrorHandler(Bus bus) {
        mBus = bus;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {

        if (cause != null) {
            mBus.post(new RestAdapterErrorEvent(cause));
        }

        return cause;
    }

}
