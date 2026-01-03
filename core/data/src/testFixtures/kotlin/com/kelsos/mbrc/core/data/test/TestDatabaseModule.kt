package com.kelsos.mbrc.core.data.test

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kelsos.mbrc.core.data.Database
import org.koin.dsl.module

/**
 * Provides an in-memory Room database for testing.
 * Use this module with testDispatcherModule in repository tests.
 */
val testDatabaseModule =
  module {
    single {
      Room
        .inMemoryDatabaseBuilder(
          ApplicationProvider.getApplicationContext(),
          Database::class.java
        ).allowMainThreadQueries()
        .build()
    }
    // DAOs
    single { get<Database>().albumDao() }
    single { get<Database>().artistDao() }
    single { get<Database>().genreDao() }
    single { get<Database>().trackDao() }
    single { get<Database>().nowPlayingDao() }
    single { get<Database>().playlistDao() }
    single { get<Database>().radioStationDao() }
    single { get<Database>().connectionDao() }
  }
