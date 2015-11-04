package com.kelsos.mbrc.di.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.utilities.BitmapConverterFactory;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class RetrofitProvider implements Provider<Retrofit> {
  @Inject private OkHttpClient client;
  @Inject private ObjectMapper mapper;

  @Override
  public Retrofit get() {

    Executor executor = Executors.newSingleThreadExecutor();
    return new Retrofit.Builder().baseUrl("http://localhost:8188")
        .addConverterFactory(BitmapConverterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).client(client)
        .callbackExecutor(executor)
        .build();
  }
}
