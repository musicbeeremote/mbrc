package com.kelsos.mbrc.data

import androidx.room.Database
import androidx.room.RoomDatabase
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
import com.kelsos.mbrc.features.radio.data.RadioStationDao
import com.kelsos.mbrc.features.radio.data.RadioStationEntity
import com.kelsos.mbrc.data.Database.Companion.VERSION
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
  ],
  version = VERSION
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
    const val VERSION = 1
  }
}