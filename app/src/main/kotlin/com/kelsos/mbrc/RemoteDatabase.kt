package com.kelsos.mbrc

import com.kelsos.mbrc.content.library.tracks.Track
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
  const val VERSION = 3
  const val NAME = "cache"

  @Migration(version = 3, database = RemoteDatabase::class)
  class LibraryTableMigration : AlterTableMigration<Track>(Track::class.java) {
    override fun onPreMigrate() {
      super.onPreMigrate()
      addColumn(SQLiteType.TEXT, "year")
    }
  }
}
