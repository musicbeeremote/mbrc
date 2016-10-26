package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.ApiService
import retrofit2.Retrofit
import javax.inject.Inject

class ApiProvider
@Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<ApiService>(retrofit, ApiService::class.java)
