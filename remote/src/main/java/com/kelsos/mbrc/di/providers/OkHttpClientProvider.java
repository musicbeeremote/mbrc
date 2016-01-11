package com.kelsos.mbrc.di.providers;

import android.text.TextUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.utilities.SettingsManager;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;
import roboguice.util.Ln;

public class OkHttpClientProvider implements Provider<OkHttpClient> {

  @Inject private SettingsManager manager;

  @Override public OkHttpClient get() {

    Ln.d("[Provider] returning OkHttpClient");
    return new OkHttpClient.Builder().addInterceptor(chain -> {
      final long start = System.currentTimeMillis();
      final ConnectionSettings settings = manager.getDefault();
      Request request = chain.request();

      final Request.Builder builder = request.newBuilder().header("Accept", "application/json");

      if (!TextUtils.isEmpty(settings.getAddress())) {
        final HttpUrl url = request.url().newBuilder().host(settings.getAddress()).port(settings.getHttp()).build();
        builder.url(url);
      }

      request = builder.build();
      Ln.v("[Interceptor] Sending Request to [%s]", request.url());
      final Response response = chain.proceed(request);
      final long end = System.currentTimeMillis();
      Ln.v("[Interceptor] Request Complete [%s] :: duration [%d] ms :: code %d ",
          request.url(),
          (end - start),
          response.code());

      return response;
    }).readTimeout(40, TimeUnit.SECONDS)
        .connectTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build();
  }
}
