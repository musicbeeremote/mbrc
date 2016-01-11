package com.kelsos.mbrc.di.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;

import retrofit2.Retrofit;

public class ApiServiceProvider<T> implements Provider<T> {
  @Inject private Retrofit retrofit;
  private Class<T> serviceClass;

  public ApiServiceProvider (Class<T> serviceClass) {
    this.serviceClass = serviceClass;
  }

  @Override
  public T get() {
    return retrofit.create(serviceClass);
  }
}
