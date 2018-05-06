package com.kelsos.mbrc

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kelsos.mbrc.Database.Companion.VERSION
import com.kelsos.mbrc.content.library.albums.AlbumDao
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistDao
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreDao
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackDao
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.nowplaying.NowPlayingDao
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.content.playlists.PlaylistDao
import com.kelsos.mbrc.content.playlists.PlaylistEntity
import com.kelsos.mbrc.content.radios.RadioStationDao
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.networking.connections.ConnectionDao
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

@Database(
  entities = [
    GenreEntity::class,
    ArtistEntity::class,
    AlbumEntity::class,
    TrackEntity::class,
    NowPlayingEntity::class,
    PlaylistEntity::class,
    RadioStationEntity::class,
    ConnectionSettingsEntity::class
  ], version = VERSION
)
abstract class Database : RoomDatabase() {

  abstract fun genreDao(): GenreDao

  abstract fun artistDao(): ArtistDao

  abstract fun albumDao(): AlbumDao

  abstract fun trackDao(): TrackDao

  abstract fun nowPlayingDao(): NowPlayingDao

  abstract fun playlistDao(): PlaylistDao

  abstract fun radioStationDao(): RadioStationDao

  abstract fun connectionDao(): ConnectionDao

  companion object {
    const val VERSION = 5
  }
}

