package com.kelsos.mbrc

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.now_playing.NowPlaying
import com.kelsos.mbrc.content.playlists.Playlist
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration

@Database(version = RemoteDatabase.VERSION, name = RemoteDatabase.NAME)
object RemoteDatabase {
  const val VERSION = 4
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

  @Migration(version = 4, database = RemoteDatabase::class)
  class LibraryTableMigration : AlterTableMigration<Track>(Track::class.java) {
    override fun onPreMigrate() {
      super.onPreMigrate()
      addColumn(SQLiteType.TEXT, "year")
    }
  }
}
