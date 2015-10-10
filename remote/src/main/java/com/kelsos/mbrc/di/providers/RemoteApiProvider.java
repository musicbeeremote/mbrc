package com.kelsos.mbrc.di.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.RemoteEndPoint;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class RemoteApiProvider implements Provider<RemoteApi> {

  @Inject private RemoteEndPoint endPoint;
  @Inject private OkHttpClient client;
  @Inject private ObjectMapper mapper;

  @Override
  public RemoteApi get() {

    Executor executor = Executors.newSingleThreadExecutor();
    client.interceptors().add(chain -> {
      Request request = chain.request();
      request = request.newBuilder()
          .addHeader("Accept", "application/json")
          .build();
      return chain.proceed(request);
    });

    final Retrofit retrofit = new Retrofit.Builder().baseUrl(endPoint.getUrl())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(client)
        .callbackExecutor(executor)
        .build();

    return retrofit.create(RemoteApi.class);
  }
}
