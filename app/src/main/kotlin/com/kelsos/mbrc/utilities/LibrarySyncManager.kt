package com.kelsos.mbrc.utilities

import android.content.SharedPreferences
import com.google.inject.Inject
import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dao.*
import com.kelsos.mbrc.dto.PaginatedResponse
import com.kelsos.mbrc.dto.library.CoverDto
import com.kelsos.mbrc.dto.playlist.PlaylistDto
import com.kelsos.mbrc.dto.playlist.PlaylistTrack
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.mappers.*
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.library.*
import com.kelsos.mbrc.services.api.LibraryService
import com.kelsos.mbrc.services.api.PlaylistService
import rx.Observable
import rx.functions.Func1
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import timber.log.Timber

class LibrarySyncManager {
  @Inject private lateinit var service: LibraryService
  @Inject private lateinit var albumRepository: AlbumRepository
  @Inject private lateinit var artistRepository: ArtistRepository
  @Inject private lateinit var genreRepository: GenreRepository
  @Inject private lateinit var trackRepository: TrackRepository
  @Inject private lateinit var coverRepository: CoverRepository
  @Inject private lateinit var downloader: CoverDownloader
  @Inject private lateinit var preferences: SharedPreferences
  @Inject private lateinit var playlistService: PlaylistService
  @Inject private lateinit var playlistRepository: PlaylistRepository

  fun sync() {
    GenreDao_Table.index_genre_name_index.createIfNotExists()
    AlbumDao_Table.index_album_name_index.createIfNotExists()
    ArtistDao_Table.index_artist_name_index.createIfNotExists()
    TrackDao_Table.index_track_title_index.createIfNotExists()
    PlaylistDao_Table.index_playlist_name_index.createIfNotExists()


    val after = preferences.getLong(LAST_SYNC, 0)
    Observable.create<Any> {
      syncGenres(after)
      syncArtists(after)
      syncCovers(after)
      syncAlbums(after)
      syncTracks(after)

      coverRepository.getAllObservable()
          .subscribeOn(Schedulers.immediate())
          .subscribe({ downloader.download(it) }, { Timber.v(it, "Failed") })

      syncPlaylistTrackInfo(after)
      syncPlaylists(after)

      playlistRepository.getPlaylists()
          .subscribeOn(Schedulers.immediate())
          .observeOn(Schedulers.immediate())
          .concatMap { it.toObservable() }
          .subscribe({ syncPlaylistTracks(it.id, after) }, { Timber.v(it, "Failed") })

      it.onCompleted()
    }.io()
        .subscribe({ }, { this.handlerError(it) },
            {
              preferences.edit().putLong(LAST_SYNC, System.currentTimeMillis() / 1000).apply()
            }
        )
  }

  private fun handlerError(throwable: Throwable) {
    Timber.e(throwable, "Error ")
  }

  private fun syncTracks(after: Long) {
    val artists = artistRepository.getAll()
    val genres = genreRepository.getAll()
    val albums = albumRepository.getAll()
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap {
          service.getLibraryTracks(LIMIT * it!!, LIMIT, after)
        }.subscribeOn(Schedulers.immediate())
        .takeWhile({ this.canGetNext(it) })
        .subscribe(
            {
              val daos = TrackMapper.mapDtos(it.data, artists, genres, albums)
              trackRepository.save(daos)
            }, { this.handlerError(it) }, { })
  }

  private fun syncGenres(after: Long) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap { service.getLibraryGenres(LIMIT * it!!, LIMIT, after) }
        .subscribeOn(Schedulers.immediate())
        .takeWhile({ this.canGetNext(it) })
        .subscribe({ genreRepository.save(GenreMapper.map(it.data)) }) { this.handlerError(it) }
  }

  private fun syncArtists(after: Long) {
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap { service.getLibraryArtists(LIMIT * it!!, LIMIT, after) }
        .subscribeOn(Schedulers.immediate())
        .takeWhile({ this.canGetNext(it) })
        .subscribe({ artistRepository.save(ArtistMapper.map(it.data)) }) { this.handlerError(it) }
  }

  private fun canGetNext(page: PaginatedResponse<*>): Boolean {
    val isSuccessful = page.code == Code.SUCCESS
    val data = page.data.size
    val retrieved = page.offset + data
    return data > 0 && retrieved <= page.total && isSuccessful
  }

  private fun syncAlbums(after: Long) {
    val cachedCovers = coverRepository.getAll()
    val cachedArtists = artistRepository.getAll()
    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap { service.getLibraryAlbums(LIMIT * it!!, LIMIT, after) }
        .subscribeOn(Schedulers.immediate())
        .takeWhile({ this.canGetNext(it) })
        .subscribe(
            {
              val daos = AlbumMapper.mapDtos(it.data, cachedCovers, cachedArtists)
              albumRepository.save(daos)
            }) { this.handlerError(it) }
  }

  private fun syncCovers(after: Long) {
    Observable.range(0,
        Integer.MAX_VALUE - 1).concatMap<PaginatedResponse<CoverDto>>(Func1 {
      service.getLibraryCovers(LIMIT * it!!,
          LIMIT,
          after)
    }).subscribeOn(Schedulers.immediate()).takeWhile({ this.canGetNext(it) }).subscribe(
        { coverRepository.save(CoverMapper.map(it.data)) },
        { this.handlerError(it) })
  }

  private fun syncPlaylists(after: Long) {
    Observable.range(0,
        Integer.MAX_VALUE - 1).concatMap<PaginatedResponse<PlaylistDto>>(Func1 {
      playlistService.getPlaylists(LIMIT * it!!,
          LIMIT,
          after)
    }).subscribeOn(Schedulers.immediate()).takeWhile({ this.canGetNext(it) }).subscribe(
        { playlistRepository.savePlaylists(PlaylistMapper.mapDto(it.data)) },
        { this.handlerError(it) })
  }

  private fun syncPlaylistTracks(playlistId: Long, after: Long) {
    Observable.range(0,
        Integer.MAX_VALUE - 1).concatMap<PaginatedResponse<PlaylistTrack>>(Func1 {
      playlistService.getPlaylistTracks(playlistId,
          LIMIT * it!!,
          LIMIT,
          after)
    }).subscribeOn(Schedulers.immediate()).takeWhile({
      this.canGetNext(it)
    }).subscribe({
      val data = PlaylistTrackMapper.map(it.data,
          { id: Long -> playlistRepository.getPlaylistById(id) },
          { id: Long -> playlistRepository.getTrackInfoById(id) })
      playlistRepository.savePlaylistTracks(data)
    }, { this.handlerError(it) })
  }

  private fun syncPlaylistTrackInfo(after: Long) {
    Observable.range(0,
        Integer.MAX_VALUE - 1).concatMap<PaginatedResponse<PlaylistTrackInfo>>(Func1 {
      playlistService.getPlaylistTrackInfo(LIMIT * it!!,
          LIMIT,
          after)
    }).subscribeOn(Schedulers.immediate()).takeWhile({
      this.canGetNext(it)
    }).subscribe({ playlistRepository.savePlaylistTrackInfo(PlaylistTrackInfoMapper.map(it.data)) },
        { this.handlerError(it) })
  }

  companion object {
    const val LIMIT = 400
    const val LAST_SYNC = "last_sync"
  }
}
