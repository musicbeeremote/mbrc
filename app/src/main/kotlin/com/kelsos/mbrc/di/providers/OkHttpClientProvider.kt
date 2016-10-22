package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.utilities.SettingsManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.annotation.Nullable
import javax.inject.Inject
import javax.inject.Provider

class OkHttpClientProvider : Provider<OkHttpClient> {

  @Inject private lateinit var manager: SettingsManager
  @Inject @Nullable private var interceptor: Interceptor? = null

  override fun get(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    val accept = Interceptor {
      val settings = manager.default
      val request = it.request()
      val builder = request.newBuilder().header("Accept", "application/json")

      if (settings != null && settings.address.isNullOrBlank().not()) {
        val url = request.url()
            .newBuilder()
            .host(settings.address)
            .port(settings.port)
            .build()
        builder.url(url)
      }

      it.proceed(builder.build())
    }
    val builder = OkHttpClient.Builder()

    interceptor?.let { builder.addNetworkInterceptor(it) }

    return builder
        .addInterceptor(accept)
        .addInterceptor(loggingInterceptor)
        .readTimeout(40, TimeUnit.SECONDS)
        .connectTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()
  }
}
