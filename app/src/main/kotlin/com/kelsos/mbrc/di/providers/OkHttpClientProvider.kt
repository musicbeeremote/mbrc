package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.repository.ConnectionRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class OkHttpClientProvider
@Inject constructor(private val connectionRepository: ConnectionRepository,
                    private val interceptor: Interceptor? = null) : Provider<OkHttpClient> {

  override fun get(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    val accept = Interceptor {
      val settings = connectionRepository.default
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
