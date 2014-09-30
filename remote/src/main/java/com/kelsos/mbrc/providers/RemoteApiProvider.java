package com.kelsos.mbrc.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.rest.RemoteApi;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoteApiProvider implements Provider<RemoteApi> {

    @Inject
    RestAdapter.Builder builder;

    @Override
    public RemoteApi get() {

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
            }
        };

        Executor executor = Executors.newSingleThreadExecutor();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(RemoteApi.API_URL)
                .setConverter(RemoteApi.DATA_CONVERTER)
                .setExecutors(executor, executor)
                .setRequestInterceptor(interceptor)
                .build();

        return restAdapter.create(RemoteApi.class);
    }
}
