package com.kelsos.mbrc.data.db

import com.kelsos.mbrc.library.albums.Album
import com.kelsos.mbrc.library.artists.Artist
import com.kelsos.mbrc.library.genres.Genre
import com.kelsos.mbrc.library.tracks.Track
import com.kelsos.mbrc.now_playing.NowPlaying
import com.kelsos.mbrc.playlists.Playlist
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
  const val VERSION = 3
  const val NAME = "cache"

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3Genre(table: Class<Genre>) : AlterTableMigration<Genre>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3Artist(table: Class<Artist>) : AlterTableMigration<Artist>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3Album(table: Class<Album>) : AlterTableMigration<Album>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.TEXT, "cover")
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3Track(table: Class<Track>) : AlterTableMigration<Track>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3NowPlaying(table: Class<NowPlaying>) : AlterTableMigration<NowPlaying>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }

  @Migration(version = 3, database = RemoteDatabase::class)
  class Migration3Playlist(table: Class<Playlist>) : AlterTableMigration<Playlist>(table) {
    override fun onPreMigrate() {
      addColumn(SQLiteType.INTEGER, "date_added")
    }
  }
}
