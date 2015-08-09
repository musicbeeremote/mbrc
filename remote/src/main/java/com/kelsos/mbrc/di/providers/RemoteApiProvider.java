package com.kelsos.mbrc.di.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.converter.JacksonConverter;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.RemoteEndPoint;
import com.squareup.okhttp.OkHttpClient;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import roboguice.util.Ln;

public class RemoteApiProvider implements Provider<RemoteApi> {

  @Inject private RestAdapter.Builder builder;

  @Inject private RemoteEndPoint endPoint;

  @Inject private OkHttpClient client;

  @Inject private JacksonConverter converter;

  @Override public RemoteApi get() {

    RequestInterceptor interceptor = request -> request.addHeader("Accept", "application/json");

    Executor executor = Executors.newSingleThreadExecutor();

    final RestAdapter.Builder builder = this.builder.setEndpoint(endPoint)
        .setConverter(converter)
        .setClient(new OkClient(client))
        .setExecutors(executor, executor)
        .setRequestInterceptor(interceptor);

    if (BuildConfig.DEBUG) {
      builder.setLog(Ln::v);
    }

    RestAdapter restAdapter = builder
        .build();

    return restAdapter.create(RemoteApi.class);
  }
}
