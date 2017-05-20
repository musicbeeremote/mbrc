package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.extensions.toPage
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import io.reactivex.Observable
import javax.inject.Inject

class LibraryServiceImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    settingsManager: SettingsManager
) : ServiceBase(repository, mapper, settingsManager), LibraryService {

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
