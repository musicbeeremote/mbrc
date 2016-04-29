package com.kelsos.mbrc.di.providers

import android.text.TextUtils
import com.google.inject.Inject
import com.google.inject.Provider
import com.kelsos.mbrc.utilities.SettingsManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

class OkHttpClientProvider : Provider<OkHttpClient> {

  @Inject private lateinit var manager: SettingsManager

  override fun get(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    val accept = Interceptor {
      val settings = manager.default.toBlocking().first()
      val request = it.request()
      val builder = request.newBuilder().header("Accept", "application/json")

      if (settings != null && !TextUtils.isEmpty(settings.address)) {
        val url = request.url().newBuilder().host(settings.address).port(settings.http).build()
        builder.url(url)
      }

      it.proceed(builder.build())
    }
    return OkHttpClient.Builder()
        .addInterceptor(accept)
        .addInterceptor(loggingInterceptor)
        .readTimeout(40, TimeUnit.SECONDS)
        .connectTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()
  }
}
