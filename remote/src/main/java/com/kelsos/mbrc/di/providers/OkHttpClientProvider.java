package com.kelsos.mbrc.di.providers;

import android.text.TextUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class OkHttpClientProvider implements Provider<OkHttpClient> {

  @Inject private SettingsManager manager;

  @Override public OkHttpClient get() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(msg -> Timber.tag("OkHttp").d(msg));
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

    Interceptor accept = chain -> {
      final DeviceSettings settings = manager.getDefault().toBlocking().first();
      Request request = chain.request();
      final Request.Builder builder = request.newBuilder().header("Accept", "application/json");

      if (settings != null && !TextUtils.isEmpty(settings.getAddress())) {
        final HttpUrl url = request.url().newBuilder().host(settings.getAddress()).port(settings.getHttp()).build();
        builder.url(url);
      }

      return chain.proceed(builder.build());
    };
    return new OkHttpClient.Builder().addInterceptor(accept)
        .addInterceptor(loggingInterceptor)
        .readTimeout(40, TimeUnit.SECONDS)
        .connectTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build();
  }
}
