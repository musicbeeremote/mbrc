package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.PlayerService
import retrofit2.Retrofit
import javax.inject.Inject

class PlayerServiceProvider
@Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<PlayerService>(retrofit, PlayerService::class.java)
