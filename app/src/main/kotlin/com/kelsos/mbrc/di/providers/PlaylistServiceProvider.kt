package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.PlaylistService
import retrofit2.Retrofit
import javax.inject.Inject

class PlaylistServiceProvider
@Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<PlaylistService>(retrofit, PlaylistService::class.java)
