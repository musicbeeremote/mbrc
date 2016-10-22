package com.kelsos.mbrc.di.providers

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.utilities.BitmapConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Provider

class RetrofitProvider : Provider<Retrofit> {
  @Inject private lateinit var client: OkHttpClient
  @Inject private lateinit var mapper: ObjectMapper

  override fun get(): Retrofit {

    val executor = Executors.newSingleThreadExecutor()
    return Retrofit.Builder()
        .baseUrl("http://localhost:8188")
        .addConverterFactory(BitmapConverterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(client)
        .callbackExecutor(executor)
        .build()
  }
}
