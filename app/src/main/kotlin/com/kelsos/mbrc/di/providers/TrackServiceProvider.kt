package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.TrackService
import retrofit2.Retrofit
import javax.inject.Inject

class TrackServiceProvider
@Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<TrackService>(retrofit, TrackService::class.java) {
}
