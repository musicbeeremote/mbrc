package com.kelsos.mbrc.di.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.utilities.BitmapConverterFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitProvider implements Provider<Retrofit> {
  @Inject private OkHttpClient client;
  @Inject private ObjectMapper mapper;

  @Override
  public Retrofit get() {

    Executor executor = Executors.newSingleThreadExecutor();
    return new Retrofit.Builder().baseUrl("http://localhost:8188")
        .addConverterFactory(BitmapConverterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(client)
        .callbackExecutor(executor)
        .build();
  }
}
