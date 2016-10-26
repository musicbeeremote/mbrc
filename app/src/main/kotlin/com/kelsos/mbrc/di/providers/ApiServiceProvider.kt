package com.kelsos.mbrc.di.providers

import retrofit2.Retrofit
import javax.inject.Provider

abstract class ApiServiceProvider<T>
constructor(private val retrofit: Retrofit, private val classType: Class<T>) : Provider<T> {
  override fun get(): T {
    return retrofit.create(classType)
  }
}
