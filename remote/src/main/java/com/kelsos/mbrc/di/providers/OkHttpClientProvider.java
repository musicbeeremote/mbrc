package com.kelsos.mbrc.di.providers;

import android.text.TextUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

import roboguice.util.Ln;

public class OkHttpClientProvider implements Provider<OkHttpClient> {

  @Inject private SettingsManager manager;

  @Override public OkHttpClient get() {
    final OkHttpClient httpClient = new OkHttpClient();
    httpClient.setConnectTimeout(40, TimeUnit.SECONDS);
    httpClient.setReadTimeout(40, TimeUnit.SECONDS);
    httpClient.setWriteTimeout(40, TimeUnit.SECONDS);

    httpClient.interceptors().add(chain -> {
      final long start = System.currentTimeMillis();
      final ConnectionSettings settings = manager.getDefault();
      Request request = chain.request();

      final Request.Builder builder = request.newBuilder()
          .addHeader("Accept", "application/json");

      if (!TextUtils.isEmpty(settings.getAddress())) {
        final HttpUrl url = request.httpUrl()
            .newBuilder()
            .host(settings.getAddress())
            .port(settings.getHttp())
            .build();

        builder.url(url);
      }

      request = builder.build();
      Ln.v("[Interceptor] Sending Request to [%s]", request.httpUrl());
      final Response response = chain.proceed(request);
      final long end = System.currentTimeMillis();
      Ln.v("[Interceptor] Request Complete [%s] :: duration [%d] ms :: code %d ", request.httpUrl(), (end - start), response.code());

      return response;
    });

    return httpClient;
  }
}
