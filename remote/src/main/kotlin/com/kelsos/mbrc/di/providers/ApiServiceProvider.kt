package com.kelsos.mbrc.di.providers

import com.google.inject.Inject
import com.google.inject.Provider

import retrofit2.Retrofit

class ApiServiceProvider<T>(private val serviceClass: Class<T>) : Provider<T> {
  @Inject private lateinit var retrofit: Retrofit

  override fun get(): T {
    return retrofit.create(serviceClass)
  }
}
