package com.kelsos.mbrc.di.providers;

import com.google.inject.Provider;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.interceptors.LoggingInterceptor;
import com.squareup.okhttp.OkHttpClient;

public class OkHttpClientProvider implements Provider<OkHttpClient> {
  @Override public OkHttpClient get() {
    final OkHttpClient httpClient = new OkHttpClient();
    if (BuildConfig.DEBUG) {
      httpClient.interceptors().add(new LoggingInterceptor());
    }
    return httpClient;
  }
}
