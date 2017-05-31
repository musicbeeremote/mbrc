package com.kelsos.mbrc.content.library

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class LibraryApiImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ApiBase(repository, mapper, settingsManager), LibraryService {

  override fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseGenres, range).flatMap { it.toPage<Genre>(mapper) }
  }

  override fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseArtists, range).flatMap { it.toPage<Artist>(mapper) }
  }

  override fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseAlbums, range).flatMap { it.toPage<Album>(mapper) }
  }

  override fun getTracks(offset: Int, limit: Int): Observable<Page<Track>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseTracks, range).flatMap { it.toPage<Track>(mapper) }
  }
}
