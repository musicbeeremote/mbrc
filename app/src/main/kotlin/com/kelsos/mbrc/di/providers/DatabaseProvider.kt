package com.kelsos.mbrc.di.providers

import android.app.Application
import androidx.room.Room
import com.kelsos.mbrc.RemoteDb
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
constructor(private val application: Application) : Provider<RemoteDb> {
  override fun get(): RemoteDb {
    val context = application.applicationContext
    return Room.databaseBuilder(context, RemoteDb::class.java, "cache").build()
  }
}

class GenreDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<GenreDao> {
  override fun get(): GenreDao = remoteDb.genreDao()
}

class ArtistDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<ArtistDao> {
  override fun get(): ArtistDao = remoteDb.artistDao()
}

class AlbumDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<AlbumDao> {
  override fun get(): AlbumDao = remoteDb.albumDao()
}

class TrackDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<TrackDao> {
  override fun get(): TrackDao = remoteDb.trackDao()
}

class NowPlayingDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<NowPlayingDao> {
  override fun get(): NowPlayingDao = remoteDb.nowPlayingDao()
}

class PlaylistDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<PlaylistDao> {
  override fun get(): PlaylistDao = remoteDb.playlistDao()
}

class RadioStationDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<RadioStationDao> {
  override fun get(): RadioStationDao = remoteDb.radioStationDao()
}

class ConnectionDaoProvider
@Inject
constructor(private val remoteDb: RemoteDb) : Provider<ConnectionDao> {
  override fun get(): ConnectionDao = remoteDb.connectionDao()
}
