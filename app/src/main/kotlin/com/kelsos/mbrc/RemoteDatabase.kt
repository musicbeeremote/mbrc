package com.kelsos.mbrc

import com.kelsos.mbrc.content.library.tracks.Track
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import timber.log.Timber

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
  const val VERSION = 4
  const val NAME = "cache"

  @Migration(version = 3, database = RemoteDatabase::class)
  class LibraryTableMigration : AlterTableMigration<Track>(Track::class.java) {
    override fun onPreMigrate() {
      super.onPreMigrate()
      addColumn(SQLiteType.TEXT, "year")
    }
  }

  @Migration(version = 4, database = RemoteDatabase::class)
  class LibraryTableMigration3to4 : BaseMigration() {
    override fun migrate(database: DatabaseWrapper) {
      Timber.v("Running migration from 3 to 4")
      database.execSQL("DROP TABLE IF EXISTS track")
      database.execSQL("CREATE TABLE IF NOT EXISTS `track`(`artist` TEXT, `title` TEXT, `src` TEXT UNIQUE ON CONFLICT REPLACE, `trackno` INTEGER, `disc` INTEGER, `album_artist` TEXT, `album` TEXT, `genre` TEXT, `year` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT)")
    }
  }
}
