package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.NowPlayingService
import retrofit2.Retrofit
import javax.inject.Inject

class NowPlayingServiceProvider  @Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<NowPlayingService>(retrofit, NowPlayingService::class.java)

