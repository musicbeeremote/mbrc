package com.kelsos.mbrc.content.library

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.content.library.albums.AlbumDto
import com.kelsos.mbrc.content.library.artists.ArtistDto
import com.kelsos.mbrc.content.library.genres.GenreDto
import com.kelsos.mbrc.content.library.tracks.TrackDto
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import javax.inject.Inject

class LibraryApiImpl
@Inject
constructor(
  repository: ConnectionRepository,
  private val mapper: ObjectMapper,
  clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), LibraryService {

  override fun getGenres(offset: Int, limit: Int): Observable<Page<GenreDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseGenres, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<GenreDto>>(it.data as String) }
    }
  }

  override fun getArtists(offset: Int, limit: Int): Observable<Page<ArtistDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseArtists, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<ArtistDto>>(it.data as String) }
    }
  }

  override fun getAlbums(offset: Int, limit: Int): Observable<Page<AlbumDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseAlbums, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<AlbumDto>>(it.data as String) }
    }
  }

  override fun getTracks(offset: Int, limit: Int): Observable<Page<TrackDto>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseTracks, range).flatMap {
      return@flatMap Observable.fromCallable { mapper.readValue<Page<TrackDto>>(it.data as String) }
    }
  }
}