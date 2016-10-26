package com.kelsos.mbrc.di.providers

import com.kelsos.mbrc.services.api.LibraryService
import retrofit2.Retrofit
import javax.inject.Inject

class LibraryServiceProvider @Inject constructor(retrofit: Retrofit) :
    ApiServiceProvider<LibraryService>(retrofit, LibraryService::class.java)
