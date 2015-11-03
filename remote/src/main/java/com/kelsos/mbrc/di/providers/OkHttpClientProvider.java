package com.kelsos.mbrc.di.providers;

import android.text.TextUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.interceptors.LoggingInterceptor;
import com.kelsos.mbrc.rest.RemoteEndPoint;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.TimeUnit;

import roboguice.util.Ln;

public class OkHttpClientProvider implements Provider<OkHttpClient> {
  @Inject private RemoteEndPoint endPoint;

  @Override public OkHttpClient get() {
    final OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(40, TimeUnit.SECONDS);
    httpClient.setReadTimeout(40, TimeUnit.SECONDS);
    httpClient.setWriteTimeout(40, TimeUnit.SECONDS);

    if (BuildConfig.DEBUG) {
      httpClient.interceptors().add(new LoggingInterceptor());
      httpClient.interceptors().add(chain -> {
        Request request = chain.request();

        if (!TextUtils.isEmpty(endPoint.getAddress())) {
          final HttpUrl url = request.httpUrl()
              .newBuilder()
              .host(endPoint.getAddress())
              .port(endPoint.getHttpPort())
              .build();
          Ln.v(url.toString());
          request = request.newBuilder().url(url).build();
        }

        return chain.proceed(request);
      });
    }
    return httpClient;
  }
}
