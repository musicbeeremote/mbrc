package com.kelsos.mbrc.core.data

import android.content.Context
import androidx.room.Room
import com.kelsos.mbrc.core.data.migration.DefaultConnectionMigration
import com.kelsos.mbrc.core.data.migration.MigrationManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for core data dependencies.
 *
 * This module provides:
 * - Room database instance
 * - All DAOs for data access
 * - Migration utilities
 *
 * Required dependencies from other modules:
 * - Android Context (provided by Koin Android)
 */
val dataModule = module {
  single {
    Room
      .databaseBuilder(get<Context>(), Database::class.java, Database.NAME)
      .addMigrations(MIGRATION_3_4)
      .build()
  }

  // DAOs
  single { get<Database>().genreDao() }
  single { get<Database>().artistDao() }
  single { get<Database>().albumDao() }
  single { get<Database>().trackDao() }
  single { get<Database>().nowPlayingDao() }
  single { get<Database>().playlistDao() }
  single { get<Database>().radioStationDao() }
  single { get<Database>().connectionDao() }

  // Migration utilities
  singleOf(::DefaultConnectionMigration)
  singleOf(::MigrationManager)
}
