package com.kelsos.mbrc.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kelsos.mbrc.data.Database.Companion.VERSION
import com.kelsos.mbrc.features.library.data.AlbumDao
import com.kelsos.mbrc.features.library.data.AlbumEntity
import com.kelsos.mbrc.features.library.data.ArtistDao
import com.kelsos.mbrc.features.library.data.ArtistEntity
import com.kelsos.mbrc.features.library.data.GenreDao
import com.kelsos.mbrc.features.library.data.GenreEntity
import com.kelsos.mbrc.features.library.data.TrackDao
import com.kelsos.mbrc.features.library.data.TrackEntity
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity
import com.kelsos.mbrc.features.playlists.PlaylistDao
import com.kelsos.mbrc.features.playlists.PlaylistEntity
import com.kelsos.mbrc.features.radio.RadioStationDao
import com.kelsos.mbrc.features.radio.RadioStationEntity
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
    const val V1 = 1
    const val V2 = 2
    const val V3 = 3
    const val V4 = 4
    const val VERSION = 5
  }
}
