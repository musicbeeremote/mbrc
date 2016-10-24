package com.kelsos.mbrc.di.providers

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Provider

class ApiServiceProvider<T>(private val serviceClass: Class<T>) : Provider<T> {
  @Inject lateinit var retrofit: Retrofit

  override fun get(): T {
    return retrofit.create(serviceClass)
  }
}
