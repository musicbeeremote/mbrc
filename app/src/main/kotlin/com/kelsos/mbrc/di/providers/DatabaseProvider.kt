package com.kelsos.mbrc.di.providers

import android.app.Application
import androidx.room.Room
import com.kelsos.mbrc.Database
import com.kelsos.mbrc.content.library.albums.AlbumDao
import com.kelsos.mbrc.content.library.artists.ArtistDao
import com.kelsos.mbrc.content.library.genres.GenreDao
import com.kelsos.mbrc.content.library.tracks.TrackDao
import com.kelsos.mbrc.content.nowplaying.NowPlayingDao
import com.kelsos.mbrc.content.playlists.PlaylistDao
import com.kelsos.mbrc.content.radios.RadioStationDao
import com.kelsos.mbrc.networking.connections.ConnectionDao
import javax.inject.Inject
import javax.inject.Provider

class DatabaseProvider
@Inject
constructor(private val application: Application) : Provider<Database> {
  override fun get(): Database {
    val context = application.applicationContext
    return Room.databaseBuilder(context, Database::class.java, "cache").build()
  }
}

class GenreDaoProvider
@Inject
constructor(private val database: Database) : Provider<GenreDao> {
  override fun get(): GenreDao = database.genreDao()
}

class ArtistDaoProvider
@Inject
constructor(private val database: Database) : Provider<ArtistDao> {
  override fun get(): ArtistDao = database.artistDao()
}

class AlbumDaoProvider
@Inject
constructor(private val database: Database) : Provider<AlbumDao> {
  override fun get(): AlbumDao = database.albumDao()
}

class TrackDaoProvider
@Inject
constructor(private val database: Database) : Provider<TrackDao> {
  override fun get(): TrackDao = database.trackDao()
}

class NowPlayingDaoProvider
@Inject
constructor(private val database: Database) : Provider<NowPlayingDao> {
  override fun get(): NowPlayingDao = database.nowPlayingDao()
}

class PlaylistDaoProvider
@Inject
constructor(private val database: Database) : Provider<PlaylistDao> {
  override fun get(): PlaylistDao = database.playlistDao()
}

class RadioStationDaoProvider
@Inject
constructor(private val database: Database) : Provider<RadioStationDao> {
  override fun get(): RadioStationDao = database.radioStationDao()
}

class ConnectionDaoProvider
@Inject
constructor(private val database: Database) : Provider<ConnectionDao> {
  override fun get(): ConnectionDao = database.connectionDao()
}
